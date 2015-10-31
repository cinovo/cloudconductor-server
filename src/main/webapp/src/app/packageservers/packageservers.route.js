'use strict';

angular.module('cloudconductor').config(function ($routeProvider) {
	$routeProvider
	.when('/packageservers', {
		templateUrl : 'app/packageservers/packageservers.html',
		controller : 'PackageserversController',
		controllerAs : 'packageservers',
		activeNav: 'packageservers',
		siteTitle: 'Packageservers'
	});
});
