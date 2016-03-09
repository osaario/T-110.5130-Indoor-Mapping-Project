
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var PhotoSchema = new Schema({
	created: {type: Date,default: Date.now},
	description: { type: String, default: '', required: 'Title cannot be blank'},
	content: { data: Buffer, contentType: String },
	photoLocation: {type: Schema.ObjectId,ref: 'Location'},
	rotation: {type: Schema.ObjectId,ref: 'Rotation'}
});

mongoose.model('Photo', PhotoSchema);