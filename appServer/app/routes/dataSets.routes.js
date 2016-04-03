/*jshint node: true */
'use strict';

var dataSets = require('../../app/controllers/dataSets.controller');

module.exports = function(app) {

	app.route('/api/datasets')
		.get(dataSets.list)
		.post(dataSets.create);
	app.route('/api/datasets/:datasetId')
		.get(dataSets.details)
		.put(dataSets.update)
		.delete(dataSets.delete);

	app.route('/api/datasets/:datasetId/locations')
		.get(dataSets.listLocations)
		.post(dataSets.createLocation);
	app.route('/api/datasets/:datasetId/locations/:locationId')
		.put(dataSets.updateLocation)
		.delete(dataSets.deleteLocation);

	app.route('/api/datasets/:datasetId/locations/:locationId/photos')
		.get(dataSets.listPhotos)
		.post(dataSets.createPhoto);
	app.route('/api/datasets/:datasetId/locations/:locationId/photos/:photoId')
		.put(dataSets.updatePhoto)
		.delete(dataSets.deletePhoto);

	app.route('/api/images/:photoId')
		.get(dataSets.getImage)
		.post(dataSets.uploadImage);
};
