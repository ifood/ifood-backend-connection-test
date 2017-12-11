const winston = require('winston');

const LOGGER = new winston.Logger({
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

LOGGER.stream = {
  write: function (message) {
    LOGGER.log('info', '[morgan] ' + message.trim());
  }
};

module.exports = LOGGER;
