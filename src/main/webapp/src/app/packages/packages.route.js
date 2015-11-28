'use strict';

angular.module('cloudconductor').config(function ($routeProvider) {
	$routeProvider
	.when('/packages', {
		templateUrl : 'app/packages/packages.html',
		controller : 'PackagesController',
		controllerAs : 'packages',
		activeNav: 'packages',
		siteTitle: 'Packages'
	});
});
