
'use strict';

process.env.NODE_ENV = process.env.NODE_ENV || 'development';
var port =  Number(process.env.PORT || 3000);

var mongoose = require('./config/mongoose'),
	express = require('./config/express');

var db = mongoose();

var app = express();

app.listen(port);

console.log('Server running at http://localhost:3000/');

module.exports = app;