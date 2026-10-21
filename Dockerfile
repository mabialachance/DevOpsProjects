FROM node:24-alpine 
# Build a docker image called my-app

ENV MONGO_DB_USERNAME=pemb \
    MONGO_DB_PWD=Gloire06

RUN mkdir -p /home/app

COPY ./app /home/app

# set default dir so that next commands executes in /home/app dir
WORKDIR /home/app

RUN npm install
EXPOSE 3000
# no need for /home/app/server.js because of WORKDIR/ launch the the node server.js
CMD ["node", "server.js"]
