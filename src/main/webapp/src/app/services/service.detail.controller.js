'use strict';

angular.module('cloudconductor').controller(
		'ServiceDetailController',
		[
				'$scope',
				'$http',
				'$routeParams',
				function(scope, http, routeParams) {
					var that = this;
					that.routePara = routeParams;

					http.get('/api/services/' + routeParams.serviceName).then(
							function(res) {
								that.service = res.data;
							}, function(err) {
								console.log(err);
							});
				} ]);