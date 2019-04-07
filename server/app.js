const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');

const app = express();

app.use(bodyParser.json({
  limit: '8mb'
})); // support json encoded bodies

app.use(bodyParser.urlencoded({
  limit: '8mb',
  extended: true
})); // support encoded bodies

const logger = require('./modules/logger');

app.use('/', express.static(path.join(__dirname, './public')));

app.use(function(req, res, next) {
  logger.info(req.originalUrl);
  next();
});

app.get('/', function(req, res) {
  res.redirect('https://github.com/varunon9/SaathMeTravel');
});

app.post('/extranet/feedback', function(req, res) {
  logger.info(req.body);
  res.json({
    success: true,
    message: 'Thank you for your feedback.'
  });
});

module.exports = app;
