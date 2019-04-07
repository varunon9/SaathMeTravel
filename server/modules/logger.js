const bunyan = require('bunyan');
const fs = require('fs');

const env = process.env.NODE_ENV || 'prod';
const config = require('../config/config.js')[env];

const logDirectory = config.logDirectory;

if (!fs.existsSync(logDirectory)) {
  // Create the directory if it does not exist
  fs.mkdirSync(logDirectory);
}

let streams;
if (env === 'dev') {
  streams = [
    {
      level: 'debug',
      stream: process.stdout
    }
  ];
} else {
  streams = [
    {
      level: 'info',
      path: `${logDirectory}/saathmetravel-info.log` // log INFO and above to a file
    },
    {
      level: 'error',
      path: `${logDirectory}/saathmetravel-error.log` // log ERROR and above to a file
    }
  ];
}

/**
 * Log levels- fatal(60), error(50), warn(40), info(30), debug(20), trace(10)
 * Setting a logger instance (or one of its streams) to a particular level
 * implies that all log records at that level and above are logged.
 * E.g. a logger set to level "info" will log records at level info
 * and above (warn, error, fatal).
 */
const logger = bunyan.createLogger({
  name: 'saathmetravel',
  streams,
  serializers: bunyan.stdSerializers
});

module.exports = logger;
