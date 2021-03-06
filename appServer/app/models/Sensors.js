/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var SensorSchema = new Schema({
	created: {type:Date, default:Date.now},
	from: {type:Schema.ObjectId, ref:'Location'},
	timestamp: [{type:Number}],
	xOrientation: [{type:Number}],
	yOrientation: [{type:Number}],
	zOrientation: [{type:Number}],
	xGyroscope: [{type:Number}],
	yGyroscope: [{type:Number}],
	zGyroscope: [{type:Number}],
	xMagnetic: [{type:Number}],
	yMagnetic: [{type:Number}],
	zMagnetic: [{type:Number}],
	xAccelerometer: [{type:Number}],
	yAccelerometer: [{type:Number}],
	zAccelerometer: [{type:Number}],
	xCoordinate: [{type:Number}],
	yCoordinate: [{type:Number}],
	zCoordinate: [{type:Number}],
});

mongoose.model('Sensor', SensorSchema);
