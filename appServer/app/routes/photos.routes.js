
'use strict';

var photos = require('../../app/controllers/photos.controller');

module.exports = function(app) {

	app.route('/api/photos')
	   .get(photos.list)
	   .post(photos.create);
	

	app.route('/api/photos/:photoId')
	   .get(photos.read)
	   .put(photos.update)
	   .delete(photos.delete);

	app.param('photoId', photos.photoByID);
};