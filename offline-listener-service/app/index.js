const LOG = require('./helpers/logger');
LOG.info("Log configured sucessfully");

const express = require('express');
const app = express();

app.use(require("morgan")(( (app.get('env') === 'development') ? 'dev' : 'combined'), {"stream": LOG.stream}));

const cookieParser = require('cookie-parser');
app.use(cookieParser());

const bodyParser = require('body-parser');
app.use(bodyParser.json());

const cors = require('cors');
app.use(cors());

/* ***************************** ROUTE CONFIGURATION */
const routes = require('./routes');

/* HEALTH CHECK */
app.use('/heartbeat', routes.heartbeat);


// start listener
require('./services/offline-listener');


/* ERROR HANDLING */
/// catch 404 and forward to error handler
app.use((req, res, next) => {
  let err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// production error handler
// no stacktraces leaked to user
app.use((err, req, res, next) => {  // eslint-disable-line
  let st = (err.status || 500);

  if( st >= 500 ){
    LOG.error(err);
  } else {
    LOG.warn(err);
  }

  res.status(st);

  if( app.get('env') === 'development' ){
    res.contentType("text/plain");
    res.send(err.stack);
  }  else {
    res.send();
  }

});


module.exports = {
  app: app,
  logger: LOG
};
