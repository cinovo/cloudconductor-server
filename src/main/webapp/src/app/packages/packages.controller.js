'use strict';

angular.module('cloudconductor')
.controller('PackagesController', [ '$scope', '$http', function(scope, http) {
	var ctrl = this;
	http.get('/api/packages').then(function(res) {
		ctrl.list = res.data;
	}, function(err) {
		console.log(err);
	});
}]);
