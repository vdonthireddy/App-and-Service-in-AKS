
const express = require('express');
const request = require('request');
var app = express();


app.get("/", (req, res) => {
    res.send("Hello AKS!!!");
});

app.get("/names", (req, res) => {
    request("http://vjservice-service/got", function(error, response, body) {
        console.log('body: ' + body);
        res.send(body);
    });
});

app.listen(80, '0.0.0.0');