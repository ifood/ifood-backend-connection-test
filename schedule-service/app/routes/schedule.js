const express = require('express');
const wobj = require('../helpers/with-obj').withObj;
const which = require('../helpers/which').which;
const LOG = require('../helpers/logger');
const shortid = require('shortid');
const mongodb = require('../helpers/mongodb');

let collection;
mongodb.connect().then((db) => {
  collection = db.collection('schedules');
});

const router = express.Router();

router.route('/')
  .post((req, resp) => {

    let body = req.body;

    let assert = which(body).isNull('start', 'end');
    if( assert.length > 0 ){
      LOG.debug(`Propriedades requeridas nao enviadas: ${assert}`);
      resp.status(400).send({
        status: 400,
        msg: 'Either start and end are required'
      });
      return;
    }

    if( !body.type || (body.type !== "AU" && body.type !== "UN") ){
      resp.status(400).send({
        status: 400,
        msg: '"type" must be "AU" or "UN"'
      });
      return;
    }

    // limpando a entrada do usuario
    let record = wobj()
      .add('id', shortid.generate())
      .add('start', body.start)
      .add('end', body.end)
      .add('restaurant', 10)
      .get();

    LOG.info(`Salvando agendamento: \n${JSON.stringify(record, null, 2)}\n`);

    collection.insertOne(record)
      .then(() => {
        resp.status(200).send({ id : record.id });
      });

  });

module.exports = router;
