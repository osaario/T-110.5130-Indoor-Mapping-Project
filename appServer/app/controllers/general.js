/*jshint node: true */
'use strict';

var NotFound = exports.NotFound = function() {
	var err = new Error('Not found');
	err.name = 'NotFound';
	return err;
};

exports.onSuccess = function(next, success) {
	return function(err, doc) {
		if (err) return next(err);
		if (!doc) return next(new NotFound());
		if (typeof(success.json) === 'function') {
			success.json(doc);
		} else {
			success(doc);
		}
	};
};

exports.error = function(err, req, res, next) {
	if (err.name == 'NotFound') {
		res.status(404).json({'error':'Not found'});
	} else {
		return next(err);
	}
};

function listRecursion(list, eachCallback, finalCallback, i) {
	i = i || 0;
	if (i < list.length) {
		eachCallback(i, function() {
			listRecursion(list, eachCallback, finalCallback, i+1);
		});
	} else {
		finalCallback();
	}
}
exports.foreach = listRecursion;
