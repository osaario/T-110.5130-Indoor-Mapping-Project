
'use strict';


var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var LocationSchema = new Schema({
	xCoordinate: { type: Number, required: 'X-Coordinate cannot be blank'},
	yCoordinate: { type: Number, required: 'Y-Coordinate cannot be blank'},
	zCoordinate: { type: Number, required: 'Z-Coordinate cannot be blank'},
	dataSet: {type: Schema.ObjectId,ref: 'DataSet'},
	photo: {type: Schema.ObjectId,ref: 'Photo'},
	previousLocation: {type: Schema.ObjectId,ref: 'Location', default: null}
});

mongoose.model('Location', LocationSchema);