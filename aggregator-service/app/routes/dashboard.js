const LOG = require('../helpers/logger');
const express = require('express');
const mongodb = require('../helpers/mongodb');
const socket = require('../helpers/socket');

let collection;
mongodb.connect()
  .then((db) => {
    collection = db.collection('online');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const router = express.Router();

router.route('/restaurants')
  .get((req, resp) => {

    collection.find({}).toArray((err, docs) => {
      if (!err && docs.length > 0) {
        resp.status(200).send(docs.map(e => {
          return {
            id: e.clientId,
            event: e.event
          };
        }));
      } else {
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
