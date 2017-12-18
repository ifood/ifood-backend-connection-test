const os = require('process');

const
  REDIS_HOST = os.env.REDIS_HOST || 'localhost',
  RABBIT_HOST = os.env.RABBIT_HOST || 'localhost';

const express = require('express');
const router = express.Router();
const redis = require('redis');
const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');
const withobj = require('../helpers/with-obj').withObj;
const which = require('../helpers/which').which;

let redisClient = redis.createClient({
  host: REDIS_HOST,
  port: 6379
});

let __REDIS_READY = false;
let __RABBIT_READY = false;

const EVENT_CHANNEL_NAME = "KEEPALIVE_EVENTS";
let eventChannel;

redisClient.on('ready', () => {
  __REDIS_READY = true;
  LOG.info("Redis connected");
});
redisClient.on('error', (err) => {
  LOG.error("Fail to connect to redis", err);
});

let conntrials = 0;
let rabbitconn = setInterval(() => {
  rabbit.connect('amqp://' + RABBIT_HOST, (err, conn) => {
  if (!err) {
    LOG.info(`Conectado ao RabbitMQ [${RABBIT_HOST}]`);
    conn.createChannel((err, ch) => {
      ch.assertExchange(EVENT_CHANNEL_NAME, 'fanout', { durable: false });
      eventChannel = ch;
      __RABBIT_READY = true;
      clearInterval(rabbitconn);
    });
  } else {
    LOG.error("Falha ao tentar conectar no RabbitMQ [" + conntrials + "]", err);
    conntrials++;
  }
  });
}, 5000);

router.route('/available')
  .post((req, resp) => {
    process('AV', req, resp);
  });

router.route('/unavailable')
  .post((req, resp) => {
    process('UN', req, resp);
  });

let process = (status, req, resp) => {
  let client = Builders.clientInfo(req);
  client.status = status;

  LOG.debug(`Cliente ${client.clientId}, status ${client.status}`);
  let cacheObj = {
    unavailable: client.status === 'UN'
  };

  if( client.status === 'UN' ){
    cacheObj.lastUnavailability = new Date().getTime();
  }

  redisClient.exists(client.clientId, (err, exists) => {
    if( !err && exists ){
      redisClient.hmset(client.clientId, cacheObj, (err) => {
        if (err) {
          LOG.error("Falha ao tentar gravar hash: " + cacheObj, err);
          resp.status(500).send(err);
        } else {
          try {
            Utils.dispatchEvent(Builders.event(client.clientId, client.status === 'AV' ? 'AVAILABLE' : 'UNAVAILABLE'));
            resp.status(204).send();
          } catch (e) {
            resp.status(500).send(err);
          }
        }
      });
    } else {
      if( err ){
        LOG.error(`Falha ao tentar verificar existencia de ${client.clientId}`, err);
        resp.status(500).send(Builders.error(500, 'Unable to process status change'));
      } else {
        LOG.info(`Tentativa de atualizar status de restaurante offilne. ClientId ${client.clientId}, Status ${client.status}`);
        resp.status(404).send();
      }
    }
  });


};

router.route('/:clientId')
  .get((req, resp) => {

    let clientId = req.params.clientId;
    LOG.debug(`ClientID do parametro: ${clientId}`);

    redisClient.hgetall(clientId, (err, ret) => {
      if (err) {
        LOG.error(`${clientId} nao pode ser recuperado. Erro: `, err);
        resp.status(500).send(err);
      } else {
        if( ret === null ){
          resp.status(404).send();
        } else {
          resp.status(200).send(ret);
        }
      }
    });
  });

module.exports = router;

let Builders = {

  clientInfo(req) {
    return withobj({})
      .add('clientId', req.body.clientId)
      .add('status', req.body.status)
      .get();
  },

  event(clientId, eventName, timestamp) {
    return withobj({})
      .add('clientId', "" + clientId)
      .add('name', eventName)
      .add('timestamp', (timestamp) ? timestamp : new Date().getTime())
      .get();
  },

  error(code, message) {
    return withobj({})
      .add('code', code)
      .add('message', message)
      .get();
  }

};

let Utils = {
  dispatchEvent(event) {
    let validEvent = which(event.name)
      .isOneOf('AVAILABLE', 'UNAVAILABLE');

    if (!validEvent) {
      throw `O evento ${event.name} nao eh valido`;
    } else {
      eventChannel.publish(EVENT_CHANNEL_NAME, '', new Buffer(JSON.stringify(event)));
    }
  }
};
