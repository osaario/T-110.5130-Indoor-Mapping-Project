/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	config = require('../../config/config');

var DataSetSchema = new Schema({
	created: {type:Date, default:Date.now},
	name: {type:String, default:'Unknown'},
	description: {type:String, default:''},
	mapPhoto: {type:Schema.ObjectId, ref:'Photo'},
});

mongoose.model('DataSet', DataSetSchema);
