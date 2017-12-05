const mongodb = require('mongodb').MongoClient;
const LOG = require('./logger');

const DATASTORE_SERVER = process.env.DATASTORE_SERVER || "localhost";
const DATASTORE_PORT = process.env.DATASTORE_PORT || 27017;
const DATASTORE_DBNAME = process.env.DATASTORE_DBNAME || "dashboard";

const DATASTORE_URL = process.env.DATASTORE_URL || `mongodb://${DATASTORE_SERVER}:${DATASTORE_PORT}/${DATASTORE_DBNAME}`;

const config = {
  dbUrl : DATASTORE_URL
};

let mongodbWrapper = {
  status: "NOT_CONNECTED",
  errorMessage: "",
  connect: async () => {
    try {
      let db = await mongodb.connect(config.dbUrl);
      mongodbWrapper.status = "CONNECTED";
      delete mongodbWrapper.errorMessage;
      return db;
    } catch (e) {
      LOG.error("******", e);
      mongodbWrapper.errorMessage = e.message;
    }
  }
};

module.exports = mongodbWrapper;
