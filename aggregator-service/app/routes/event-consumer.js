const LOG = require('../helpers/logger');
const rabbit = require('amqplib/callback_api');
const mongodb = require('../helpers/mongodb');

let collection;
mongodb.connect()
  .then((db) => {
    collection = db.collection('online');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const EVENT_CHANNEL_NAME = "KEEPALIVE_EVENTS";

rabbit.connect('amqp://localhost', (err, conn) => {
  if (!err) {
    LOG.info("RabbitMQ, eh nois!");
    conn.createChannel((err, ch) => {
      ch.assertQueue(EVENT_CHANNEL_NAME);

      ch.consume(EVENT_CHANNEL_NAME, (msg) => {

        let obj = JSON.parse(msg.content.toString());

        collection.findOneAndUpdate(
          {
            clientId: obj.clientId
          },
          {
            $set : { event : obj.event }
          },
          {
            returnOriginal: false,
            upsert: true
          }, (err, result) => {
            if( !err ){
              LOG.info(`Registro de clientId ${obj.clientId} atualizado. Result: ${result.ok}, event: ${obj.event}`);
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
