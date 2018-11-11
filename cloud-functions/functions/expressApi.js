// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// to access google direction and place APIs
const googleMapsClient = require('@google/maps').createClient({
  key: functions.config().google_maps.routes_places_api_key,
  Promise: Promise
});

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();

app.use(bodyParser.json({
    limit: '8mb'
})); // support json encoded bodies

// Automatically allow cross-origin requests
app.use(cors({ origin: true }));

// Add middleware to authenticate requests todo: use token
app.use((req, res, next) => {
  const userAgent = req.get('User-Agent');
  if (isUserAgentMobile(userAgent)) {
    return next();
  } else {
    return res.status(400)
      .json({
        success: false,
        message: 'Not Authorized'
      });
  }
});

app.get('/directions', (req, res) => {
  const query = req.query;
  console.log(query);
  if (query.origin && query.destination) {
    googleMapsClient.directions(query).asPromise()
      .then((response) => {
        console.log(response.json);
        return res.status(200)
          .json({
            success: true,
            result: response.json
          });
      })
      .catch((error) => {
        console.error(error);
        return res.status(500)
          .json({
            success: false,
            message: error.message
          });
      });
  } else {
    return res.status(400)
      .json({
        success: false,
        message: 'Missing origin or destination params'
      });
  }
});

function isUserAgentMobile(userAgent) {
  let isMobile = false;

  // Windows Phone must come first because its UA also contains "Android"
  if (/windows phone/i.test(userAgent)) {
    isMobile = true;
  } else if (/android/i.test(userAgent)) {
    isMobile = true;
  } else if (/iPad|iPhone|iPod/.test(userAgent)) {
    isMobile = true;
  }

  return isMobile;
}


// Expose Express API as a single Cloud Function:
module.exports = app;
