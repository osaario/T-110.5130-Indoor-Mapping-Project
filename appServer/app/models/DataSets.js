/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var DataSetSchema = new Schema({
	created: {type:Date, default:Date.now},
	name: {type:String, default:'Unknown'},
	description: {type:String, default:''},
});

mongoose.model('DataSet', DataSetSchema);
