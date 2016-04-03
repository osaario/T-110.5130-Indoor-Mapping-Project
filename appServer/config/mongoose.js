/*jshint node: true */
'use strict';

var	config = require('./config'),
	mongoose = require('mongoose');

module.exports = function() {

	var db = mongoose.connect(config.db);

	require('../app/models/Photos');
	require('../app/models/Locations');
	require('../app/models/DataSets');

	return db;
};
