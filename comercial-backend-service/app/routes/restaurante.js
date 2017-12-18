const LOG = require('../helpers/logger');
const express = require('express');
const mongodb = require('../helpers/mongodb');
const socket = require('../helpers/socket');

let disponibilidade;

mongodb.connect()
  .then((db) => {
    disponibilidade = db.collection('disponibilidade');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const router = express.Router();

router.route('/:id')
  .get((req, resp) => {

    disponibilidade.findOne({restaurante_id: req.params.id}, (err, restaurante) => {
      // aqui ja virou relaxo =/
      resp.status(200).send(restaurante);
    });

  });

module.exports = router;
