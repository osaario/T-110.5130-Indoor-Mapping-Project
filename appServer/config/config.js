/*jshint node: true */
'use strict';

var config = {
	env: 'development',
	db: 'mongodb://localhost/indoormapping',
	//url: 'http://localhost:3000/',
	url: 'http://192.168.0.10:3000/',
};

if (process.env.MONGOLAB_URI !== undefined) {
	config.env = 'production';
	config.db = process.env.MONGOLAB_URI;
	config.url = 'https://indoor-mapping-app-server.herokuapp.com/';
}

module.exports = config;
