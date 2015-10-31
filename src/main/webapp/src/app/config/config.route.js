'use strict';

angular.module('cloudconductor').config(function ($routeProvider) {
	$routeProvider
	.when('/config/:templateid', {
		templateUrl : 'app/config/config.html',
		controller : 'ConfigController',
		controllerAs : 'config',
		activeNav: 'config',
		siteTitle: 'Config'
	});
});


//.when('/config/:templateid', {
//	templateUrl : 'app/config/config.html',
//	controller : 'ConfigController',
//	controllerAs : 'Config',
//	activeNav: 'config',
//	siteTitle: 'Config'
//})