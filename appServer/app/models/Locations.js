/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var LocationSchema = new Schema({
	created: {type:Date, default:Date.now},
	xCoordinate: {type:Number, default:0},
	yCoordinate: {type:Number, default:0},
	zCoordinate: {type:Number, default:0},
	name: {type:String, default:''},
	description: {type:String, default:''},
	photos: [{type:Schema.ObjectId, ref:'Photo'}],
	paths: [{type:Schema.ObjectId, ref:'Sensor'}],
});

LocationSchema.pre('remove', function(next) {
	this.model('Photo').find({'_id': {$in:this.photos}}, function(err, photos) {
		for (var i in photos) {
			photos[i].remove();
		}
	});
	this.model('Sensor').find({'_id': {$in:this.paths}}, function(err, paths) {
		for (var i in paths) {
			paths[i].remove();
		}
	});
	next();
});

mongoose.model('Location', LocationSchema);
