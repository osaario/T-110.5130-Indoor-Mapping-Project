'use strict';

var dataSets = require('../../app/controllers/dataSets.controller');

module.exports = function(app) {

	app.route('/api/datasets')
	   .get(dataSets.list)
	   .post(dataSets.create);

	app.route('/api/datasets/:datasetId/locations')
		.get(dataSets.listLocations)
		.post(dataSets.createLocation);

	app.route('/api/datasets/:datasetId/locations/:locationId/photos')
		.get(dataSets.listPhotos)
		.post(dataSets.createPhoto);

};