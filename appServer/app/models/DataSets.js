
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var LocationSchema = new Schema({
	name: { type: String, default: 'first floor'},
	description: { type: String, default: 'CS first floor plan'},
});

mongoose.model('DataSet', DataSetSchema);