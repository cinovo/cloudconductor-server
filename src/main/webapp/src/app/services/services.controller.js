'use strict';

angular.module('cloudconductor').controller('ServicesController',
		[ '$scope', '$http', function(scope, http) {
			var that = this;
			http.get('/api/services').then(function(res) {
				that.list = res.data;
			}, function(err) {
				console.log(err);
			});
		} ]);