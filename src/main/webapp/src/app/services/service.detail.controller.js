'use strict';

angular.module('cloudconductor').controller(
		'ServiceDetailController',
		[
				'$scope',
				'$rootScope',
				'$http',
				'$routeParams',
				function(scope, rootScope, http, routeParams) {
					var that = this;

					http.get('/api/services/' + routeParams.serviceName).then(
							function(res) {
								that.service = res.data;
								rootScope.siteTitle = 'Service Detail - ' + that.service.name;
							}, function(err) {
								console.log(err);
							});
					this.save = function() {
						console.log('Save', that.service);
					}
				} ]);