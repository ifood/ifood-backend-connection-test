const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');
const mongodb = require('../helpers/mongodb');
const socket = require('../helpers/socket');

let collection;
mongodb.connect()
  .then((db) => {
    collection = db.collection('realtime_state');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const EVENT_CHANNEL_NAME = "STREAM_REALTIME_DASHBOARD";

rabbit.connect('amqp://localhost', (err, conn) => {
  if (!err) {
    LOG.info("RabbitMQ, eh nois!");
    conn.createChannel((err, ch) => {
      ch.assertQueue(EVENT_CHANNEL_NAME);

      ch.consume(EVENT_CHANNEL_NAME, (msg) => {

        let event = JSON.parse(msg.content.toString());

        LOG.debug("[realtime_state] Recebendo estado: " + event.name + ", para restid: " + event.clientId);

        collection.findOneAndUpdate(
          {
            restaurante_id: event.clientId
          },
          {
            $set: {state: event.name, last_updated: event.timestamp}
          },
          {
            returnOriginal: false,
            upsert: false
          }, (err, result) => {
            if (!err) {
              LOG.info(`[realtime_state] Registro de clientId ${event.clientId} atualizado. Result: ${result.ok}, event: ${event.name}`);
              socket.sender('updatechart', {id: event.clientId, state: event.name});
            } else {
              LOG.error("MongoDB???? Ta me tirando? ", err);
            }
          });

      }, {noAck: true});


    });
  } else {
    LOG.error("RabbitMQ, nao eh nois =(", err);
  }
});

module.exports = {};
