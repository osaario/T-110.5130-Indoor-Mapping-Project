/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	DataSet = mongoose.model('DataSet'),
	PhotoLocation = mongoose.model('Location'),
	Photo = mongoose.model('Photo'),
	H = require('../models/helpers');

exports.render = function(req, res, next) {
	DataSet.find().exec(H.onSuccess(next, function(dataSets) {
		res.render('index', {
			title: 'Indoor Mapping App Server',
			sets: dataSets,
		});
	}));
};
