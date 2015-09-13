(function() {
	'use strict';

	angular.module('cloudconductor').config(routeConfig);

	function routeConfig($routeProvider) {
		$routeProvider.when('/', {
			templateUrl : 'app/main/main.html',
			controller : 'MainController',
			controllerAs : 'main',
			activeNav: 'home',
			siteTitle: 'Dashboard'
		}).otherwise({
			redirectTo : '/'
		});
	}

})();
