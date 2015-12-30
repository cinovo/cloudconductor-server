(function() {
	'use strict';

	angular.module('cloudconductor').config(function($routeProvider) {
		$routeProvider.when('/packageservers', {
			templateUrl : 'app/packageservers/overview.html',
			controller : 'PackageserversController',
			controllerAs : 'packageservers',
			activeNav : 'packageservers',
			siteTitle : 'Packageservers'
		}).when('/packageservers/edit/:name', {
			templateUrl : 'app/packageservers/edit.html',
			controller : 'PSGModController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageserver'
		}).when('/packageservers/new', {
			templateUrl : 'app/packageservers/edit.html',
			controller : 'PSGNewController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageserver'
		}).when('/packageservers/:name/edit/:server', {
			templateUrl : 'app/packageservers/edit.html',
			controller : 'PSModController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageservers'
		}).when('/packageservers/:name/new', {
			templateUrl : 'app/packageservers/edit.html',
			controller : 'PSNewController',
			controllerAs : 'controller',
			activeNav : 'packageservers',
			siteTitle : 'Packageserver'
		});
	});

})()
