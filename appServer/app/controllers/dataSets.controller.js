/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	DataSet = mongoose.model('DataSet'),
	PhotoLocation = mongoose.model('Location'),
	Photo = mongoose.model('Photo'),
	File = mongoose.model('File'),
	Sensor = mongoose.model('Sensor'),
	H = require('../models/helpers'),
	lwip = require('lwip');

var extend = require('util')._extend;

exports.list = function(req, res, next) {
	DataSet.find().populate('mapPhoto').exec(H.onSuccess(next, res));
};

exports.create = function(req, res, next) {
	DataSet.create(req.body, H.onSuccess(next, res));
};

exports.update = function(req, res, next) {
	DataSet.findByIdAndUpdate(req.params.datasetId, req.body, H.onSuccess(next, res));
};

exports.delete = function(req, res, next) {
	// Must use model.remove() to trigger middlewares.
	DataSet.findById(req.params.datasetId, H.onSuccessRemove(next, res));
};

exports.listLocations = function(req, res, next) {
	DataSet.findById(req.params.datasetId, H.onSuccess(next, function(dataSet) {
		PhotoLocation.find({'_id':{$in:dataSet.locations}})
			.populate('photos paths').exec(H.onSuccess(next, res));
	}));
};

exports.createLocation = function(req, res, next) {
	DataSet.findById(req.params.datasetId, H.onSuccess(next, function(dataSet) {
		PhotoLocation.create(req.body, H.onSuccess(next, function(location) {
			dataSet.locations.push(location._id);
			dataSet.save(H.onSuccess(next, res, location));
		}));
	}));
};

exports.updateLocation = function(req, res, next) {
	PhotoLocation.findByIdAndUpdate(req.params.locationId, req.body, H.onSuccess(next, res));
};

exports.deleteLocation = function(req, res, next) {
	DataSet.findById(req.params.datasetId, H.onSuccess(next, function(dataSet) {
		PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
			dataSet.locations.remove(location._id);
			dataSet.save(H.onSuccessRemove(next, res, location));
		}));
	}));
};

exports.listPhotos = function(req, res, next) {
	PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
		Photo.find({'_id':{$in:location.photos}}).exec(H.onSuccess(next, res));
	}));
};

exports.createPhoto = function(req, res, next) {
	PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
		Photo.create(req.body, H.onSuccess(next, function(photo) {
			location.photos.push(photo._id);
			location.save(H.onSuccess(next, function(location) {

				if (req.body.sensor !== undefined) {
					Sensor.create(req.body.sensor, H.onSuccess(next, function(sensor) {
						photo.sensor = sensor._id;
						photo.save(H.onSuccess(next, res, photo));
					}));
				} else {
					res.json(photo);
				}
			}));
		}));
	}));
};

exports.updatePhoto = function(req, res, next) {
	Photo.findByIdAndUpdate(req.params.photoId, req.body, H.onSuccess(next, res));
};

exports.deletePhoto = function(req, res, next) {
	PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
		Photo.findById(req.params.photoId, H.onSuccess(next, function(photo) {
			location.photos.remove(photo._id);
			location.save(H.onSuccessRemove(next, res, photo));
		}));
	}));
};

exports.listPaths = function(req, res, next) {
	PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
		Sensor.find({'_id':{$in:location.paths}}).exec(H.onSuccess(next, res));
	}));
};

exports.createPath = function(req, res, next) {
	PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
		Sensor.create(req.body, H.onSuccess(next, function(sensor) {
			location.paths.push(sensor._id);
			location.save(H.onSuccess(next, res, location));
		}));
	}));
};

exports.deletePath = function(req, res, next) {
	PhotoLocation.findById(req.params.locationId, H.onSuccess(next, function(location) {
		Sensor.findById(req.params.pathId, H.onSuccess(next, function(path) {
			location.paths.remove(path._id);
			location.save(H.onSuccessRemove(next, res, path));
		}));
	}));
};

exports.getImage = function(req, res, next) {
	Photo.findById(req.params.photoId).exec(H.onSuccess(next, function(photo) {
		if (photo.file !== undefined) {
			File.findById(photo.file).exec(H.onSuccess(next, function(file) {
				res.contentType(file.contentType).send(file.data);
			}));
		} else {
			res.status(404).json({'error':'Missing photo file'});
		}
	}));
};

exports.getImageScaled = function(req, res, next) {
	Photo.findById(req.params.photoId).exec(H.onSuccess(next, function(photo) {
		if (photo.file !== undefined) {
			File.findById(photo.file).exec(H.onSuccess(next, function(file) {
				lwip.open(file.data, 'jpg', H.onSuccess(next, function(image) {
					var cb = H.onSuccess(next, function(image) {
						image.toBuffer('jpg', H.onSuccess(next, function(buffer) {
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
			}));
		} else {
			res.status(404).json({'error':'Missing photo file'});
		}
	}));
};

exports.uploadImage = function(req, res, next) {
	Photo.findById(req.params.photoId).exec(H.onSuccess(next, function(photo) {
		req.busboy.on('file', function(fieldname, file, filename, encoding, contentType) {
			var size = 0;
			file.fileRead = [];
			file.on('data', function(chunk) {
				size += chunk.length;
				file.fileRead.push(chunk);
			});
			file.on('end', function() {
				var image = new File({
					contentType: contentType,
					data: Buffer.concat(file.fileRead, size)
				});
				image.save(H.onSuccess(next, function(doc) {
					photo.file = doc._id;
					photo.save(H.onSuccess(next, res));
				}));
			});
		});
		req.pipe(req.busboy);
	}));
};

exports.details = function(req, res, next) {
	DataSet.findOne({_id:req.params.datasetId})
		.populate('mapPhoto locations')
		.exec(H.onSuccess(next, res));
};
