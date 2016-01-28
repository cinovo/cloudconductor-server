(function() {
	'use strict';

	angular.module('cloudconductor').config(ServiceRouteConfig);

	function ServiceRouteConfig($routeProvider) {
		$routeProvider.when('/services', {
			templateUrl : 'app/services/services.html',
			controller : 'ServicesController',
			controllerAs : 'controller',
			activeNav : 'services',
			siteTitle : 'Services'
		}).when('/service/:serviceName', {
			templateUrl : 'app/services/service.detail.html',
			controller : 'ServiceDetailController',
			controllerAs : 'controller',
			activeNav : 'services'
		}).when('/service/new', {
			templateUrl : 'app/services/service.detail.html',
			controller : 'ServiceDetailController',
			controllerAs : 'controller',
			activeNav : 'services'
		});
	}
})();
