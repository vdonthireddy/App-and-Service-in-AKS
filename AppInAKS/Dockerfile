FROM node:8

WORKDIR /usr/src/app

ENV SERVICE_HOST_URL = $SERVICE_HOST_URL

COPY package.json .

RUN npm install

COPY . .

EXPOSE 9123

CMD [ "npm", "start" ]