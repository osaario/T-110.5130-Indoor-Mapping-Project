/*jshint node: true */
'use strict';

var NotFound = exports.NotFound = function() {
	var err = new Error('Not found');
	err.name = 'NotFound';
	return err;
};

exports.onSuccess = function(next, success, other) {
	return function(err, doc) {
		if (err) return next(err);
		if (!doc) return next(new NotFound());
		if (typeof(success.json) === 'function') {
			success.json(other || doc);
		} else {
			success(other || doc);
		}
	};
};

exports.onSuccessRemove = function(next, res, other) {
	return exports.onSuccess(next, function(doc) {
		doc.remove(exports.onSuccess(next, res));
	}, other);
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
