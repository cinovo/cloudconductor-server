(function() {
	'use strict';

	angular.module('cloudconductor').config(PackageRouteConfig);

	function PackageRouteConfig($routeProvider) {
		$routeProvider
		.when('/packages', {
			templateUrl : 'app/packages/packages.html',
			controller : 'PackagesController',
			controllerAs : 'controller',
			activeNav: 'packages',
			siteTitle: 'Packages'
		}).when('/packages/:packageName', {
			templateUrl : 'app/packages/package.detail.html',
			controller : 'PackageDetailController',
			controllerAs : 'controller',
			activeNav : 'packages'
		}).when('/packages/new', {
			templateUrl : 'app/services/package.detail.html',
			controller : 'PackageDetailController',
			controllerAs : 'controller',
			activeNav : 'packages'
		});
	}

})();