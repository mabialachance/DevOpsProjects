FROM node:24-alpine

ENV MONGO_DB_USERNAME=pemb \
    MONGO_DB_PWD=Gloire06

RUN mkdir -p /home/app

COPY ./app /home/app

# set default dir so that next commands executes in /home/app dir
WORKDIR /home/app

# will execute npm install in /home/app because of WORKDIR
RUN npm install

# no need for /home/app/server.js because of WORKDIR
CMD ["node", "server.js"]
