const express = require('express');
const router = express.Router();
const redis = require('redis');
const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');
const withobj = require('../helpers/with-obj').withObj;
const which = require('../helpers/which').which;

let redisClient = redis.createClient({
  host: 'localhost',
  port: 6379
});

let evtSubscriber = redisClient.duplicate();

let __REDIS_READY = false;
let __RABBIT_READY = false;
let __CACHE_RECORD_EXPIRATION_IN_SECS = 15;

const EVENT_CHANNEL_NAME = "KEEPALIVE_EVENTS";
let eventChannel;

redisClient.on('ready', () => {
  __REDIS_READY = true;
  LOG.info("Redis connected");
});
redisClient.on('error', (err) => {
  LOG.error("Fail to connect to redis", err);
});

evtSubscriber.on("psubscribe", function (channel) {
  LOG.info(`To na linha! Channel: ${channel}`);
});

evtSubscriber.on("pmessage", function (pattern, channel, message) {
  if (!__RABBIT_READY) {
    LOG.warn("RabbitMQ indisponivel. Mensagem sera ignorada.");
  } else {
    try {
      Utils.dispatchEvent(Builders.event(message, 'OFFLINE', new Date().getTime()));
    } catch (e) {
      LOG.error(`Falha ao tentar publicar evento de expiracao do Redis. A mensagem '${message}' sera ignorada`, e);
      return false;
    }
  }
  LOG.debug(`Recebi: ${message} in channel ${channel}, with pattern ${pattern}`);
});

evtSubscriber.psubscribe("__key*__:*");

rabbit.connect('amqp://localhost', (err, conn) => {
  if (!err) {
    LOG.info("RabbitMQ, eh nois!");
    conn.createChannel((err, ch) => {
      ch.assertQueue(EVENT_CHANNEL_NAME);
      eventChannel = ch;
      __RABBIT_READY = true;
    });
  } else {
    LOG.error("RabbitMQ, nao eh nois =(", err);
  }
});


router.route('/ping')
  .post((req, resp) => {

    if (!__REDIS_READY) {
      resp.setRequestHeader('Retry-After', new Date(new Date().getTime() * 1000 * 10).toISOString());
      resp.status(503).send();
      return;
    }

    let client = Builders.clientInfo(req);

    redisClient.exists(client.clientId, (err, reply) => {
      if (err) {
        resp.status(500).send(Builders.error(500, 'Fail to update client status'));
      } else {

        if (!reply) {

          try {
            Utils.dispatchEvent(Builders.event(client.clientId, 'ONLINE'));
            LOG.info(`${client.clientId} just become online`);
          } catch (dispatchError) {
            LOG.error("Falha ao tentar emitir evento: ", dispatchError);
            resp.status(500).send(Builders.error(500, 'Failt to update client status'));
            return;
          }
        }

        Rules.updateKeepalive(client)
          .then( () => {
            resp.status(204).send();
          })
          .catch( (err) => {
            LOG.error(`Falha ao atualizar keepalive do cliente ${client.clientId}`, err);
            resp.status(500).send(Builders.error(500, 'Fail to update client status'));
          });

      }
    });

  });

router.route('/status')
  .post((req, resp) => {

    let client = Builders.clientInfo(req);

    !(Object.keys(Rules.statusChanges)
      .filter(e => {
        return e === client.status;
      }).length === 1) && (() => {
        resp.status(400).send(Builders.error(400, `Status ${client.status} is invalid`));
      })();

    Rules.statusChanges[client.status](client)
      .then(() => {
        resp.status(204).send();
      })
      .catch((err) => {
        resp.status(500).send(err);
      });

  });

module.exports = router;

let Builders = {

  clientInfo(req) {
    return withobj({})
      .add('clientId', req.body.clientId)
      .add('timestamp', req.body.timestamp)
      .add('status', req.body.status)
      .get();
  },

  event(clientId, eventName, timestamp) {
    return withobj({})
      .add('clientId', clientId)
      .add('name', eventName)
      .add('timestamp', (timestamp) ? timestamp : new Date().getTime())
      .get();
  },

  cacheObject(client, timestamp){
    return [
      client.clientId,
      'serverTimestamp', (timestamp ? timestamp : new Date().getTime()),
      'clientTimestamp', client.timestamp
    ];
  },

  error(code, message) {
    return withobj({})
      .add('code', code)
      .add('message', message)
      .get();
  }

};

let Rules = {
  statusChanges: {
    'UN': (body) => {

      return new Promise((resolve, reject) => {
        LOG.debug(`Tornando o cliente ${body.clientId} indisponivel`);
        let cacheObj = Builders.cacheObject(body);
        cacheObj.push('unavailable');
        cacheObj.push(true);

        redisClient.hmset(cacheObj, (err) => {
          if (err) {
            LOG.error("Falha ao tentar gravar hash: " + cacheObj, err);
            reject(err);
          } else {
            try {
              Utils.dispatchEvent(Builders.event(body.clientId, 'UNAVAILABLE'));
              resolve();
            } catch (e) {
              reject(e);
            }
          }
        });

      });

    },
    'AV': (body) => {

      return new Promise((resolve, reject) => {
        LOG.debug(`Retomando a disponibilidade do cliente ${body.clientId}`);
        Utils.dispatchEvent(Builders.event(body.clientId, 'AVAILABLE'));

        try {
          Utils.dispatchEvent(Builders.event(body.clientId, 'ONLINE'));
          LOG.info(`${body.clientId} just become online`);
          resolve();
        } catch (dispatchError) {
          LOG.error("Falha ao tentar emitir evento: ", dispatchError);
          reject(dispatchError);
        }
      });

    }
  },

  updateKeepalive(client) {
    return new Promise( (resolve, reject) => {
      let cacheObj = Builders.cacheObject(client);
      redisClient.hmset(cacheObj, (err) => {
        if (err) {
          LOG.error("Falha ao tentar gravar hash: " + cacheObj, err);
          reject(err);
        } else {
          redisClient.expire(client.clientId, __CACHE_RECORD_EXPIRATION_IN_SECS);
          resolve();
        }
      });
    });
  }
};

let Utils = {
  dispatchEvent(event) {
    let validEvent = which(event.name)
      .isOneOf('ONLINE', 'OFFLINE', 'AVAILABLE', 'UNAVAILABLE');

    if (!validEvent) {
      throw `O evento ${event.name} nao eh valido`;
    } else {
      eventChannel.sendToQueue(EVENT_CHANNEL_NAME, new Buffer(JSON.stringify(event)));
    }
  }
};
