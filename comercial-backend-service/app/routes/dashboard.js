const LOG = require('../helpers/logger');
const express = require('express');
const mongodb = require('../helpers/mongodb');
const socket = require('../helpers/socket');

let realtime;

mongodb.connect()
  .then((db) => {
    realtime = db.collection('realtime_state');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const router = express.Router();

router.route('/restaurants')
  .get((req, resp) => {

    let where = (req.query.group ? { grupo: req.query.group } : {});

    LOG.debug("Usando where: ", where);

    realtime.find(where).toArray((err, docs) => {
      if (!err && docs.length > 0) {

        LOG.debug(`Encontrados ${docs.length}, [${err}]`);

        resp.status(200).send(docs.map(e => {
          return {
            id: e.restaurante_id,
            event: e.state
          };
        }));

      } else {
        LOG.debug("Devolvendo vazio...");
        resp.status(200).send([]);
      }
    });

  });

router.route('/test')
  .get((req, resp) => {
    let id = req.query.id;
    let state = req.query.state;

    socket.sender('updatechart', {id: id, state: state});
    resp.status(200).send();

  });

module.exports = router;
