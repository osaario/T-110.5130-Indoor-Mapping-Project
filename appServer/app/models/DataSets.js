/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	H = require('./helpers');

var DataSetSchema = new Schema({
	created: {type:Date, default:Date.now},
	name: {type:String, default:'Unknown'},
	description: {type:String, default:''},
	locations: [{type:Schema.ObjectId, ref:'Location'}],
	mapPhoto: {type:Schema.ObjectId, ref:'Photo'},
});

DataSetSchema.pre('save', function(next) {
	if (this.mapPhoto === undefined) {
		var self = this;
		self.model('Photo').create({}, function(err, photo) {
			if (err) {
				next(err);
			} else {
				self.mapPhoto = photo._id;
				next();
			}
		});
	} else {
		next();
	}
});

DataSetSchema.pre('remove', function(next) {
	if (this.mapPhoto !== undefined) {
		this.model('Photo').findById(this.mapPhoto, function(err, photo) {
			photo.remove();
		});
	}
	this.model('Location').find({'_id':{$in:this.locations}}, function(err, locations) {
		for (var i in locations) {
			locations[i].remove();
		}
	});
	next();
});

mongoose.model('DataSet', DataSetSchema);
