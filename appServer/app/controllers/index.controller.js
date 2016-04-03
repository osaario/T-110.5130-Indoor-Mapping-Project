/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	DataSet = mongoose.model('DataSet'),
	PhotoLocation = mongoose.model('Location'),
	Photo = mongoose.model('Photo'),
	G = require('./general');

exports.render = function(req, res, next) {
	DataSet.find().exec(G.onSuccess(next, function(dataSets) {
		res.render('index', {
			title: 'Indoor Mapping App Server',
			sets: dataSets,
		});
	}));
};
