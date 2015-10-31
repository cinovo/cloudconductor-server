(function() {
	'use strict';

	angular.module('cloudconductor').config(routeConfig);

	function routeConfig($routeProvider) {
		$routeProvider.when('/services', {
			templateUrl : 'app/services/services.html',
			controller : 'ServicesController',
			controllerAs : 'services',
			activeNav : 'services',
			siteTitle : 'Services'
		}).when('/service/:serviceName', {
			templateUrl : 'app/services/service.detail.html',
			controller : 'ServiceDetailController',
			controllerAs : 'serviceDetail',
			activeNav : 'services',
			siteTitle : 'Service Detail'
		})
	}
})();
