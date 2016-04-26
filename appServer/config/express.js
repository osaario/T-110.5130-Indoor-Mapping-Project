/*jshint node: true */
'use strict';

var config = require('./config'),
	express = require('express'),
	morgan = require('morgan'),
	compress = require('compression'),
	bodyParser = require('body-parser'),
	busboy = require('connect-busboy'),
	helpers = require('../app/models/helpers');

module.exports = function() {

	var app = express();

	app.use(morgan('dev'));

	app.use(bodyParser.json());
	app.use(busboy());

	app.set('views', './app/views');
	app.set('view engine', 'ejs');

	require('../app/routes/index.routes.js')(app);
	require('../app/routes/dataSets.routes.js')(app);

	app.use(helpers.error);

	app.use(express.static('./public'));

	return app;
};
