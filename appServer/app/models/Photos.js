
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var PhotoSchema = new Schema({
	created: {type: Date,default: Date.now},
	description: { type: String, default: '', trim: true, required: 'Title cannot be blank'},
	content: { data: Buffer, contentType: String },
	location: {type: Schema.ObjectId,ref: 'Location'},
	phoneAngle: {type: Schema.ObjectId,ref: 'Rotation'}
});

mongoose.model('Photo', PhotoSchema);