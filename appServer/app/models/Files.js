/*jshint node: true */
'use strict';

var mongoose = require('mongoose'),
	Schema = mongoose.Schema;

var FileSchema = new Schema({
  contentType: String,
  data: Buffer,
});

mongoose.model('File', FileSchema);
