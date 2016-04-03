/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	DataSet = mongoose.model('DataSet'),
	PhotoLocation = mongoose.model('Location'),
	Photo = mongoose.model('Photo'),
	G = require('./general');

var extend = require('util')._extend;

exports.list = function(req, res, next) {
	DataSet.find().exec(G.onSuccess(next, res));
};

exports.create = function(req, res, next) {
	DataSet.create(req.body, G.onSuccess(next, res));
};

exports.update = function(req, res, next) {
	DataSet.findByIdAndUpdate(
		req.params.datasetId,
		req.body,
		G.onSuccess(next, res)
	);
};

exports.delete = function(req, res, next) {
	DataSet.findByIdAndRemove(
		req.params.datasetId,
		G.onSuccess(next, res)
	);
};

exports.listLocations = function(req, res, next) {
	PhotoLocation.find({dataSet:req.params.datasetId}).exec(G.onSuccess(next, res));
};

exports.createLocation = function(req, res, next) {
	DataSet.findById(req.params.datasetId, G.onSuccess(next, function(dataSet) {
		var doc = extend({}, req.body);
		doc.dataSet = dataSet._id;
		PhotoLocation.find({dataSet:dataSet._id})
			.sort({$natural:-1}).limit(1)
			.exec(function(err, locations) {
				if (locations.length > 0) doc.previousLocation = locations[0]._id;
				PhotoLocation.create(doc, G.onSuccess(next, res));
			});
	}));
};

exports.updateLocation = function(req, res, next) {
	PhotoLocation.findOneAndUpdate(
		{_id:req.params.locationId, dataSet:req.params.datasetId},
		req.body,
		G.onSuccess(next, res)
	);
};

exports.deleteLocation = function(req, res, next) {
	PhotoLocation.findOneAndRemove(
		{_id:req.params.locationId, dataSet:req.params.datasetId},
		G.onSuccess(next, res)
	);
};

exports.listPhotos = function(req, res, next) {
	Photo.find({location:req.params.locationId}).exec(G.onSuccess(next, res));
};

exports.createPhoto = function(req, res, next) {
	DataSet.findById(req.params.datasetId, G.onSuccess(next, function(dataSet) {
		PhotoLocation.findById(req.params.locationId, G.onSuccess(next, function(location) {
			var doc = extend({}, req.body);
			doc.location = location._id;
			Photo.create(doc, G.onSuccess(next, res));
		}));
	}));
};

exports.updatePhoto = function(req, res, next) {
	Photo.findOneAndUpdate(
		{_id:req.params.photoId, location:req.params.locationId},
		req.body,
		G.onSuccess(next, res)
	);
};

exports.deletePhoto = function(req, res, next) {
	Photo.findOneAndRemove(
		{_id:req.params.photoId, location:req.params.locationId},
		G.onSuccess(next, res)
	);
};

exports.getImage = function(req, res, next) {
	Photo.findById(req.params.photoId).exec(G.onSuccess(next, function(photo) {
		if (photo.image.data !== undefined) {
			res.contentType(photo.image.contentType).send(photo.image.data);
		} else {
			res.status(404).json({'error':'Missing image'});
		}
	}));
};

exports.uploadImage = function(req, res, next) {
	Photo.findById(req.params.photoId).exec(G.onSuccess(next, function(photo) {
		req.busboy.on('file', function(fieldname, file, filename, encoding, contentType) {
			var size = 0;
			file.fileRead = [];
			file.on('data', function(chunk) {
				size += chunk.length;
				file.fileRead.push(chunk);
			});
			file.on('end', function() {
				photo.image.contentType = contentType;
				photo.image.data = Buffer.concat(file.fileRead, size);
				photo.save(G.onSuccess(next, function(photo) {
					res.json({'upload':'Complete'});
				}));
			});
		});
		req.pipe(req.busboy);
	}));
};

function photoRecursion(res, next, i, out) {
	if (i < out.locations.length) {
		Photo.find({location:out.locations[i]._id}).exec(G.onSuccess(next, function(photos) {
			out.locations[i].photos = photos.map(function(o) { return o.toJSON(); });
			return photoRecursion(res, next, i+1, out);
		}));
	} else {
		res.json(out);
	}
}
exports.details = function(req, res, next) {
	DataSet.findById(req.params.datasetId, G.onSuccess(next, function(dataset) {
		var out = dataset.toJSON();
		PhotoLocation.find({dataSet:dataset._id}).exec(G.onSuccess(next, function(locations) {
			out.locations = locations.map(function(o) { return o.toJSON(); });
			return photoRecursion(res, next, 0, out);
		}));
	}));
};
