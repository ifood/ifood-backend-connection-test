# Baseado no Node 6 sobre Alpine Linux
FROM mhart/alpine-node:6

### SETUP do Ambiente
RUN apk add --no-cache make gcc g++ python bash

### SETUP do Runtime da Aplicação
RUN npm install -g gulp mocha istanbul

### SETUP do Container
WORKDIR /opt/app
COPY package.json .

### ENTRYPOINT/RUN
RUN npm install
