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
  // todo: extract token from query params and validate it
  const feedback = req.body;

  // validate that request came from Gmail using proxy assertion token
  const proxyAssertionToken = req.get('Amp4Email-Proxy-Assertion');
  // todo
  res.set({
    'Access-Control-Allow-Origin': 'https://mail.google.com',
    'AMP-Access-Control-Allow-Source-Origin': 'info@saathmetravel.com',
    'Access-Control-Allow-Source-Origin': 
      'AMP-Access-Control-Allow-Source-Origin',
    'Access-Control-Expose-Headers': 'Access-Control-Allow-Origin'
      + ', AMP-Access-Control-Allow-Source-Origin'
      + ', Access-Control-Allow-Source-Origin'
  });

  res.json({
    success: true,
    message: 'Thank you for your feedback.',
    feedback
  });
});

module.exports = app;
