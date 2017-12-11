const LOG = require('./logger');

let clients = [];

let socketWrapper = (io) => {
  LOG.info("Socket.IO em cima");

  io.on('connection', function (sock) {
    LOG.info("Socket.IO setup completed");
    clients.push(sock);
  });

};

let emit = (channel, msg) => {
  if( clients.length === 0 ){
    LOG.warn("No clients connected, ignoring...");
  } else {
    clients.forEach(socket => {
      LOG.debug(`Sending ${JSON.stringify(msg)} via channel ${channel}`);
      socket.emit(channel, msg);
    });
  }
};

module.exports = {
  socketWrapper: socketWrapper,
  sender: emit
};

