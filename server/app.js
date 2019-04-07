const express = require('express');
const path = require('path');
const app = express();

const logger = require('./modules/logger');

app.use('/', express.static(path.join(__dirname, './public')));

app.use(function(req, res, next) {
  logger.info(req.originalUrl);
  next();
});

app.get('/', function(req, res) {
  res.redirect('https://github.com/varunon9/SaathMeTravel');
});

module.exports = app;
