const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');
const mongodb = require('../helpers/mongodb');

let collection;
mongodb.connect()
  .then((db) => {
    collection = db.collection('disponibilidade');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const EVENT_CHANNEL_NAME = "STREAM_EVENT_AGGREGATOR";

rabbit.connect('amqp://localhost', (err, conn) => {
  if (!err) {
    LOG.info("RabbitMQ, eh nois!");
    conn.createChannel((err, ch) => {
      ch.assertQueue(EVENT_CHANNEL_NAME);

      ch.consume(EVENT_CHANNEL_NAME, (msg) => {

        let event = JSON.parse(msg.content.toString());
        LOG.debug(`[aggregator] recebido evento ${JSON.stringify(event)}`);

        let aux = {
          name: event.name,
          timestamp: event.timestamp
        };

        LOG.debug(`[aggregator] Item a ser incluido: ${JSON.stringify(aux)}`);

        collection.findOneAndUpdate(
          {
            restaurante_id: event.clientId
          },
          {
            $push: { eventos: aux }
          },
          {
            returnOriginal: false
          }, (err, result) => {
            if (!err) {
              LOG.debug(`[aggregator] atual: ${JSON.stringify(result)}`);
              LOG.info(`[aggregator] Registro de clientId ${event.clientId} atualizado. Result: ${result.ok}, event: ${event.name}`);
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
