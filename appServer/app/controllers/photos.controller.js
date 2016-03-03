
'use strict';
var mongoose = require('mongoose'),
	Photo = mongoose.model('Photo');

var getErrorMessage = function(err) {
	if (err.errors) {
		for (var errName in err.errors) {
			if (err.errors[errName].message) return err.errors[errName].message;
		}
	} else {
		return 'Unknown server error';
	}
};

exports.create = function(req, res) {
	var photo = new Photo(req.body);
	photo.save(function(err) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(photo);
		}
	});
};

exports.list = function(req, res) {
	Photo.find().exec(function(err, photos) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(photos);
		}
	});
};

exports.read = function(req, res) {
	res.json(req.photo);
};

exports.update = function(req, res) {
	var photo = req.photo;
	photo.description = req.body.description;
	photo.save(function(err) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(photo);
		}
	});
};

exports.delete = function(req, res) {
	var photo = req.photo;
	photo.remove(function(err) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(photo);
		}
	});
};

exports.photoByID = function(req, res, next, id) {
	Photo.findById(id).exec(function(err, photo) {
		if (err) return next(err);
		if (!photo) return next(new Error('Failed to load photo ' + id));
		req.photo = photo;
		next();
	});
};
