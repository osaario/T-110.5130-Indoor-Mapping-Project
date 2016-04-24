/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	DataSet = mongoose.model('DataSet'),
	PhotoLocation = mongoose.model('Location'),
	Photo = mongoose.model('Photo'),
	Path = mongoose.model('Path'),
	G = require('./general'),
	lwip = require('lwip');

var extend = require('util')._extend;

exports.list = function(req, res, next) {
	DataSet.find().populate('mapPhoto').exec(G.onSuccess(next, res));
};

exports.create = function(req, res, next) {
	Photo.create({}, G.onSuccess(next, function(photo) {
		var doc = extend({}, req.body);
		doc.mapPhoto = photo._id;
		DataSet.create(doc, G.onSuccess(next, function(dataSet) {
			photo.datasetId = dataSet._id;
			photo.save(G.onSuccess(next, function(doc) {
				res.json(dataSet);
			}));
		}));
	}));
};

exports.update = function(req, res, next) {
	DataSet.findByIdAndUpdate(
		req.params.datasetId,
		req.body,
		G.onSuccess(next, res)
	);
};

exports.delete = function(req, res, next) {
	Photo.remove({dataSet:req.params.datasetId}, function(err, n) {
		PhotoLocation.remove({dataSet:req.params.datasetId}, function(err, n) {
			Path.remove({dataSet:req.params.datasetId}, function(err, n) {
				DataSet.findByIdAndRemove(req.params.datasetId, G.onSuccess(next, res));
			});
		});
	});
};

exports.listLocations = function(req, res, next) {
	PhotoLocation.find({dataSet:req.params.datasetId}).populate('photos paths').exec(G.onSuccess(next, res));
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
	Photo.remove({location:req.params.locationId}, function(err, docs) {
		PhotoLocation.findOneAndRemove(
			{_id:req.params.locationId, dataSet:req.params.datasetId},
			G.onSuccess(next, res)
		);
	});
};

exports.listPaths = function(req, res, next) {
	Path.find({dataSet:req.params.datasetId}).exec(G.onSuccess(next, res));
};

exports.createPath = function(req, res, next) {
	DataSet.findById(req.params.datasetId, G.onSuccess(next, function(dataSet) {
		var doc = extend({}, req.body);
		doc.dataSet = dataSet._id;
		Path.create(doc, G.onSuccess(next, function(path) {
			if (path.toLocation) {
				PhotoLocation.findOne({_id:path.toLocation, dataSet:req.params.datasetId}, G.onSuccess(next, function(location) {
					location.paths.push(path._id);
					location.save(G.onSuccess(next, function(doc) {
						res.json(path);
					}));
				}));
			} else {
				res.json(path);
			}
		}));
	}));
};

exports.deletePath = function(req, res, next) {
	Path.findOneAndRemove(
		{_id:req.params.pathId, dataSet:req.params.datasetId},
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
			doc.dataSet = dataSet._id;
			Photo.create(doc, G.onSuccess(next, function(photo) {
				location.photos.push(photo._id);
				location.save(G.onSuccess(next, function(doc) {
					res.json(photo);
				}));
			}));
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

exports.getImageScaled = function(req, res, next) {
	Photo.findById(req.params.photoId).exec(G.onSuccess(next, function(photo) {
		if (photo.image.data !== undefined) {
			lwip.open(photo.image.data, 'jpg', G.onSuccess(next, function(image) {
				var cb = G.onSuccess(next, function(image) {
					image.toBuffer('jpg', G.onSuccess(next, function(buffer) {
						res.contentType('image/jpeg').send(buffer);
					}));
				});
				if (req.params.size == 'tiny') {
					image.cover(200, 200, cb);
				} else {
					var ratio = Math.min(1, 1920 / image.width(), 1920 / image.height());
					image.scale(ratio, cb);
				}
			}));
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

exports.details = function(req, res, next) {
	DataSet.findById(req.params.datasetId, G.onSuccess(next, function(dataset) {
		var out = dataset.toJSON();
		PhotoLocation.find({dataSet:dataset._id}).populate('photos paths').exec(G.onSuccess(next, function(locations) {
			out.locations = locations;
			res.json(out);
		}));
	}));
};
