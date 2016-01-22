(function() {
	'use strict';

	angular.module('cloudconductor').config(PackageServerRouteConfig);

	function PackageServerRouteConfig($routeProvider) {
		$routeProvider.when('/packageservers', {
			templateUrl : 'app/packageservers/overview.html',
			controller : 'PackageserversController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageservers'
		}).when('/packageservers/edit/:groupId', {
			templateUrl : 'app/packageservers/psg-mod.html',
			controller : 'PSGModController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageserver'
		}).when('/packageservers/new', {
			templateUrl : 'app/packageservers/psg-mod.html',
			controller : 'PSGModController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageserver'
		}).when('/packageservers/:groupId/edit/:serverId', {
			templateUrl : 'app/packageservers/ps-mod.html',
			controller : 'PSModController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageservers'
		}).when('/packageservers/:groupId/new', {
			templateUrl : 'app/packageservers/ps-mod.html',
			controller : 'PSModController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageserver'
		});
	}

})();
