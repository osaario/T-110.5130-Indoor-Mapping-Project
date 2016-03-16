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
	var dataSet = new DataSet(req.body);
	dataSet.save(function (err) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(dataSet);
		}
	});
};

exports.list = function(req, res) {
	DataSet.find().exec(function(err, dataSets) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(dataSets);
		}
	});
};

exports.createLocation = function(req, res) {
	var photoLocation = new Location(req.body);
	photoLocation.dataSet = req.params.datasetId;
	PhotoLocation.find({dataSet: req.params.datasetId}).limit(1).sort({$natural:-1}).exec(function(err, pLocation) {
		if (pLocation)
			photoLocation.previousLocation = pLocation._id;
	});
	photoLocation.save(function(err) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(photoLocation);
		}
	});
};

exports.listLocations = function(req, res) {
	PhotoLocation.find({dataSet: req.params.datasetId}).populate('dataSet previousLocation').exec(function(err, pLocations) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(pLocations);
		}
	});
};

exports.createPhoto = function(req, res) {
	var photo = new Photo(req.body);
	photo.photoLocation = req.params.locationId;
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

exports.listPhotos = function(req, res) {
	Photo.find({dataSet: req.params.datasetId, photoLocation: req.params.datasetId}).populate('photoLocation').exec(function(err, phtos) {
		if (err) {
			return res.status(400).send({
				message: getErrorMessage(err)
			});
		} else {
			res.json(photos);
		}
	});
};
