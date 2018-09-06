
const express = require('express');
const request = require('request');
var app = express();
const SERVICE_HOST_URL = (process.env.SERVICE_HOST_URL==undefined) ? 'http://localhost:9123' : process.env.SERVICE_HOST_URL;

app.get("/", (req, res) => {
    res.send("Hello World - Vijay!!!");
});

app.get("/names", (req, res) => {
    request(process.env.SERVICE_HOST_URL, function(error, response, body) {
        console.log("Service host URL: " + SERVICE_HOST_URL);
        console.log('body: ' + body);
        res.send(body);
    });
});

app.listen(9123, '0.0.0.0');
console.log("Service host URL: " + SERVICE_HOST_URL);