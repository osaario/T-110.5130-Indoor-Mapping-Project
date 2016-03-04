
'use strict';

exports.render = function(req, res) {
	res.render('index', {
		title: 'Welcome to Indoor Mapping App Server'
	});
};