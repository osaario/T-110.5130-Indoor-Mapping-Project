
'use strict';

var config = require('./config'),
express = require('express'),
morgan = require('morgan'),
compress = require('compression'),
bodyParser = require('body-parser'),
methodOverride = require('method-override');


module.exports = function() {

	var app = express();

	app.use(morgan('dev'));
	

	app.use(bodyParser.urlencoded({
		extended: true
	}));
	app.use(bodyParser.json());
	app.use(methodOverride());

	app.set('views', './app/views');
	app.set('view engine', 'ejs');
	
	require('../app/routes/index.routes.js')(app);
	require('../app/routes/photos.routes.js')(app);

	app.use(express.static('./public'));

	return app;
};