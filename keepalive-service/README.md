Simple application template using Node.js, express, gulp and docker.

**For use with docker**

1. Build docker image: `docker build -t example-app:1 -f .docker/build.dockerfile .`
   1. Run container in development mode: `docker run -t -p 3000:3000 example-app:1 npm run develop`
   1. Run container without nodemon: `docker run -t -p 3000:3000 example-app:1`
   1. Run tests: `docker run -t -p 3000:3000 example-app:1 npm test` 
   
**For use with docker-compose**

1. `docker-compose up`

**For use without docker**

1. `npm install` to install all dependencies
2. `npm run develop` or `gulp develop` to init application with *nodemon*

Don't forget to change application and output names to your own. Names can be found in 
`package.json` and `docker-compose.yml` files.
