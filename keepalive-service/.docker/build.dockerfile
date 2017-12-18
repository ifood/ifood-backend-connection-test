# Baseado no Node 6 sobre Alpine Linux
FROM mhart/alpine-node:8

### SETUP do Ambiente
RUN apk add --no-cache make gcc g++ python bash

### SETUP do Runtime da Aplicação
RUN npm install -g gulp mocha

### SETUP do Container
WORKDIR /opt/app
COPY . .

### ENTRYPOINT/RUN
RUN npm install

CMD ["/usr/bin/env", "node", "bin/www"]
