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
	dataSet: {type:Schema.ObjectId, ref:'DataSet'},
	previousLocation: {type:Schema.ObjectId, ref:'Location', default:null},
});

mongoose.model('Location', LocationSchema);
