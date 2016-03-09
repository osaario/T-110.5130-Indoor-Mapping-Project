
'use strict';
var mongoose = require('mongoose'),
	Photo = mongoose.model('Photo'),
	DataSet = mongoose.model('DataSet'),
	Rotation = mongoose.model('Rotation'),
	PhotoLocation = mongoose.model('Location');

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
	var objectIDs = {};

	var dataSet = new DataSet();
	dataSet.save(function (err, object) {
		objectIDs[dataSetID] = object._id;
	});

	var rotation = new Rotation(req.body.rotation);
	dataSet.save(function (err, object) {
		objectIDs[rotationID] = object._id;
	});

	var photoLocation = new Location(req.body.photoLocation);
	photoLocation.dataSet = objectIDs.dataSetID;
	PhotoLocation.find().limit(1).sort({$natural:-1}).exec(function(err, pLocation) {
		if (pLocation)
			previousLocation = pLocation._id;
	});
	photoLocation.save(function (err, object) {
		objectIDs[photoLocationID] = object._id;
	});

	var photo = new Photo(req.body.photo);
	photo.rotation = objectIDs.rotationID;
	photo.photoLocation = objectIDs.photoLocationID;
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
