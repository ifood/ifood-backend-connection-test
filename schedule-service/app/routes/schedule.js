const express = require('express');
const wobj = require('../helpers/with-obj').withObj;
const which = require('../helpers/which').which;
const LOG = require('../helpers/logger');
const shortid = require('shortid');
const mongodb = require('../helpers/mongodb');

let collection;
mongodb.connect()
  .then((db) => {
    collection = db.collection('schedules');
  })
  .catch((err) => {
    LOG.error("Falha ao conectar no MongoDB", err);
  });

const router = express.Router();

function securityCheck(req){
  return true;
}

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

  }
  )
  .get( (req, resp) => {

    if( !securityCheck(req) ){
      resp.status(403).send({
        status: 403,
        message: "Access Denied"
      });
      return;
    }

    let filter = {};
    if( req.query.restaurantId ){
      filter.restaurantId = req.query.restaurantId;
    }

    let limit = (!req.query.limit || req.query.limit > 50) ? 20 : parseInt(req.query.limit);

    collection.find(filter).limit(limit)
      .toArray((err, docs) => {
        if( !err ){
          let ret = docs.map( elem => {
            return wobj(elem)
              .del("_id")
              .get();
          } );
          resp.status(200).send(ret);
        } else {
          resp.status(500).send({
            status: 500,
            message: err
          });
        }
      });

  });

module.exports = router;
