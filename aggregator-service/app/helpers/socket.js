const LOG = require('./logger');

let socket;

let socketWrapper = (io) => {
  LOG.info("Socket.IO em cima");

  io.on('connection', function (sock) {
    LOG.info("Socket.IO setup completed");
    socket = sock;
  });

};

let emit = (channel, msg) => {
  LOG.debug(`Sending ${msg} via channel ${channel}`);
  socket.emit(channel, msg);
};

module.exports = {
  socketWrapper: socketWrapper,
  sender: emit
};

