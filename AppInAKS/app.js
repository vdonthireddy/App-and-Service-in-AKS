
const express = require('express');
const request = require('request');
var app = express();


app.get("/", (req, res) => {
    res.send("Hello AKS!!!");
});

app.get("/names", (req, res) => {
    request(process.env.SERVICE_HOST_URL, function(error, response, body) {
        console.log("Service host URL: " + process.env.SERVICE_HOST_URL);
        console.log('body: ' + body);
        res.send(body);
    });
});

app.listen(9123, '0.0.0.0');