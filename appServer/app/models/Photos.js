/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	config = require('../../config/config');

var PhotoSchema = new Schema({
	created: {type:Date, default:Date.now},
	description: {type:String, default:''},
	file: {type:Schema.ObjectId, ref:'File'},
	sensor: {type:Schema.ObjectId, ref:'Sensor'},
});

PhotoSchema.pre('remove', function(next) {
	if (this.file !== undefined) {
		this.model('File').findById(this.file, function(err, file) {
			file.remove();
		});
	}
	if (this.sensor !== undefined) {
		this.model('Sensor').findById(this.sensor, function(err, sensor) {
			sensor.remove();
		});
	}
	next();
});

PhotoSchema.set('toJSON', {
	transform: function(doc, ret, options) {
		if (ret.file !== undefined) {
			ret.image = config.url + 'api/images/' + ret._id;
		}
		return ret;
	}
});

mongoose.model('Photo', PhotoSchema);
