const os = require('process');

const
  RABBIT_HOST = os.env.RABBIT_HOST || 'localhost';

const EVENT_CHANNEL_NAME = "STREAM_EVENT_AGGREGATOR";

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

let logSummary = {
  total_ok: 0,
  total_err: 0
};

let conntrials = 0;
let rabbitconn = setInterval(() => {
  rabbit.connect('amqp://' + RABBIT_HOST, (err, conn) => {
    if (!err) {
      LOG.info(`Conectado ao RabbitMQ [${RABBIT_HOST}]`);
      clearInterval(rabbitconn);

      if (!err) {
        LOG.info("Conectado ao RabbitMQ, aguardando mensagens");
        conn.createChannel((err, ch) => {
          ch.assertQueue(EVENT_CHANNEL_NAME);

          ch.consume(EVENT_CHANNEL_NAME, (msg) => {

            let event = JSON.parse(msg.content.toString());
            LOG.debug(`Recebido evento ${JSON.stringify(event)}`);

            let aux = {
              name: event.name,
              timestamp: event.timestamp
            };

            LOG.debug(`Evento a ser incluido: ${JSON.stringify(aux)}`);

            // TODO depois de contratado: separar por data.
            collection.findOneAndUpdate(
              {restaurante_id: event.clientId},
              {$push: {eventos: aux}},
              {returnOriginal: false},
              (err, result) => {
                if (!err) {
                  LOG.debug(`Registro alterado: ${JSON.stringify(result)}`);

                  let events = result.value.eventos;
                  let tempos = calculaTempo(events);

                  collection.findOneAndUpdate(
                    {restaurante_id: event.clientId},
                    {
                      $set: {tempo_online: tempos.tempo_online, tempo_offline: tempos.tempo_offline}
                    },
                    (err2, result2) => {
                      if (!err2) {
                        LOG.debug(`Recalculo dos tempos - registro alterado: ${JSON.stringify(result2)}`);
                        logSummary.total_ok++;
                      } else {
                        LOG.error("Falha ao tentar atualizar dados agregados de tempo", err);
                        logSummary.total_err++;
                      }
                    });
                } else {
                  LOG.error("Falha ao tentar incluir evento na lista de eventos", err);
                  logSummary.total_err++;
                }
              });

          }, {noAck: true});


        });
      } else {
        LOG.error("Falha ao tentar conectar no RabbitMQ [" + conntrials + "]", err);
        conntrials++;
      }
    }
  });
}, 5000);

let timer = setInterval(() => {
  let tok = logSummary.total_ok,
    terr = logSummary.total_err;

  logSummary = {
    total_ok: 0,
    total_err: 0
  };

  if (!tok && !terr) {
    LOG.info("Sem atividade nos ultimos 20s");
  } else {
    LOG.info(`Resumo dos ultimos 20s: Total de Erros: ${terr}, Total de eventos: ${tok + terr}`);
  }

}, 20000);

module.exports = {};

/**

 1. Soma os timestamps de cada status
 2. Subtrai um do outro em valor absoluto
 3. A subtração é o total de tempo do menor valor entre as somas de cada status

 Ex:

 n = ONLINE
 f = OFFLINE


 |---.---.---.---.---.---.---|
 0   1   2   3   4   5   6   7
 x   o   o   o   x
 f   n           f   n

 Sem a conta teríamos:
 offline = 2
 online = 3

 1. soma os timestamps de cada status
 f = 0 + 4 = 4
 n = 1 + 5 = 6
 2. 6 - 4 = 2
 3. Dentre f e n, o f tem o menor valor da soma, portanto ele vale 2
 4. termina a papagaiada com as sobras do inicio (ts do primeiro elemento)
 e com o restante (diferenca entre o ts do ultimo elemento e o "agora")
 *
 * @param ev lista de eventos da base
 * @returns {{tempo_online: number, tempo_offline: number}}
 */
function calculaTempo(ev) {

  let ini = ev[0].timestamp;
  let eventos = ev.map(e => {
    return {
      name: e.name,
      timestamp: parseInt((e.timestamp - ini) / 1000)
    };
  });

  let tempo_total_relativo = eventos[eventos.length - 1].timestamp - eventos[0].timestamp;

  let soma = eventos
    .filter(e => {
      return (e.name === 'ONLINE' || e.name === 'OFFLINE');
    })
    .reduce((ant, at) => {
      if (at.name === 'ONLINE') {
        return {
          online: ant.online + at.timestamp,
          offline: ant.offline
        };
      } else {
        return {
          online: ant.online,
          offline: ant.offline + at.timestamp
        };
      }

    }, {offline: 0, online: 0});

  let valor = Math.abs(soma.online - soma.offline);
  let tempo_online = 0;
  let tempo_offline = 0;

  if (soma.online > soma.offline) {
    tempo_offline = valor;
    tempo_online = tempo_total_relativo - valor;
  } else {
    tempo_online = valor;
    tempo_offline = tempo_total_relativo - valor;
  }

  return {
    tempo_online: tempo_online,
    tempo_offline: tempo_offline
  };

}
