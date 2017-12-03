const winston = require('winston');

let logger = new winston.Logger({
  transports: [
    new winston.transports.Console({
      level: 'debug',
      handleExceptions: true,
      json: false,
      colorize: true
    })
  ],
  exitOnError: false
});

logger.stream = {
  write: function (message) {
    logger.log('info', '[morgan] ' + message.trim());
  }
};

logger.info("Log configured sucessfully");

const express = require('express');
const app = express();

app.use(require("morgan")(( (app.get('env') === 'development') ? 'dev' : 'combined'), {"stream": logger.stream}));

const cookieParser = require('cookie-parser');
app.use(cookieParser());

const bodyParser = require('body-parser');
app.use(bodyParser.json());

const cors = require('cors');
app.use(cors());

const routes = require('./routes');

/* ***************************** ROUTE CONFIGURATION */


/* HEALTH CHECK */
app.use('/heartbeat', routes.heartbeat);


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
    logger.error(err);
  } else {
    logger.warn(err);
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
  logger: logger
};
