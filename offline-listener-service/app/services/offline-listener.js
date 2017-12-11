const os = require('process');

const
  REDIS_HOST = os.env.REDIS_HOST || 'localhost',
  RABBIT_HOST = os.env.RABBIT_HOST || 'localhost';

const redis = require('redis');
const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');
const withobj = require('../helpers/with-obj').withObj;
const which = require('../helpers/which').which;

let redisClient = redis.createClient({
  host: REDIS_HOST,
  port: 6379
});

let evtSubscriber = redisClient.duplicate();

let __RABBIT_READY = false;

const EVENT_CHANNEL_NAME = "KEEPALIVE_EVENTS";
let eventChannel;

redisClient.on('ready', () => {
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
      LOG.debug(`Emitindo evento OFFLINE para ${message}`);
      Utils.dispatchEvent(Builders.event(message, 'OFFLINE', new Date().getTime()));
    } catch (e) {
      LOG.error(`Falha ao tentar publicar evento de expiracao do Redis. A mensagem '${message}' sera ignorada`, e);
      return false;
    }
  }
});

evtSubscriber.psubscribe("__key*__:*");

rabbit.connect('amqp://' + RABBIT_HOST, (err, conn) => {
  if (!err) {
    LOG.info("RabbitMQ, eh nois!");
    conn.createChannel((err, ch) => {
      ch.assertExchange(EVENT_CHANNEL_NAME, 'fanout', { durable: false });
      eventChannel = ch;
      __RABBIT_READY = true;
    });
  } else {
    LOG.error("RabbitMQ, nao eh nois =(", err);
  }
});

let Builders = {

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
      .isOneOf('OFFLINE');

    if (!validEvent) {
      throw `O evento ${event.name} nao eh valido`;
    } else {
      eventChannel.publish(EVENT_CHANNEL_NAME, '', new Buffer(JSON.stringify(event)));
    }
  }
};
