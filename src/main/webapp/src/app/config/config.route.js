(function() {
	'use strict';

	angular.module('cloudconductor').config(ConfigRouteConfig);
	
	function ConfigRouteConfig ($routeProvider) {
		$routeProvider
		.when('/config/:templateid', {
			templateUrl : 'app/config/config.html',
			controller : 'ConfigController',
			controllerAs : 'config',
			activeNav: 'config',
			siteTitle: 'Config'
		});
	}

})();
