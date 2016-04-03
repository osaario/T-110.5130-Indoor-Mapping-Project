/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	config = require('../../config/config');

var PhotoSchema = new Schema({
	created: {type:Date, default:Date.now},
	xRotation: {type:Number, default:0},
	yRotation: {type:Number, default:0},
	zRotation: {type:Number, default:0},
	description: {type:String, default:''},
	image: {data:Buffer, contentType:String, select:false},
	location: {type:Schema.ObjectId, ref:'Location'},
});

PhotoSchema.set('toJSON', {
	transform: function(doc, ret, options) {
		ret.image = config.url + 'api/images/' + ret._id;
		return ret;
	}
});

mongoose.model('Photo', PhotoSchema);
