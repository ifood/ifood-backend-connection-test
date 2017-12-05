const express = require('express');
const router = express.Router();
const redis = require('redis');
const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');

let redisClient = redis.createClient({
  host: 'localhost',
  port: 6379
});

let evtSubscriber = redisClient.duplicate();

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

evtSubscriber.on("psubscribe", function (channel) {
  LOG.info(`To na linha! Channel: ${channel}`);
});

evtSubscriber.on("pmessage", function (pattern, channel, message) {
  if( !__RABBIT_READY ){
    LOG.warn("RabbitMQ indisponivel. Mensagem sera ignorada.");
  } else {
    let event = {
      event: "OFFLINE",
      clientId: message
    };

    let serial = new Buffer(JSON.stringify(event));
    eventChannel.sendToQueue(EVENT_CHANNEL_NAME, serial);
  }
  LOG.info(`Recebi: ${message} in channel ${channel}, with pattern ${pattern}`);
});

evtSubscriber.psubscribe("__key*__:*");

rabbit.connect('amqp://localhost', (err, conn) => {
  if( !err ){
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

    if( !__REDIS_READY ) {
      resp.setRequestHeader('Retry-After', new Date(new Date().getTime() * 1000 * 10).toISOString());
      resp.status(503).send();
      return;
    }

    let id = req.body.clientId;

    redisClient.exists(id, (err, reply) => {
      if(!err){

        let arrStore = [id, 'lastHit', new Date().toISOString()];

        if( !reply ) {
          // grava e seta expiracao
          arrStore.push('onlineSince');
          arrStore.push(new Date().toISOString());

          // gera evento de online
          let event = {
            event: "ONLINE",
            clientId: "" + id,
            clientTs: req.body.timestamp
          };

          let serial = new Buffer(JSON.stringify(event));
          eventChannel.sendToQueue(EVENT_CHANNEL_NAME, serial);
          LOG.info(`${id} just become online`);

        }

        redisClient.hmset(arrStore, (err, reply) => {
          if( err ) {
            LOG.error("Falha ao tentar gravar hash: " + arrStore, err);
          } else {
            redisClient.expire(id, 10);
          }
        });

      }
    });
    resp.status(204).send();

  });

module.exports = router;
