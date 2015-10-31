'use strict';

angular.module('cloudconductor').config(routeConfig);

function routeConfig($routeProvider) {
	$routeProvider
	.when('/templates', {
		templateUrl : 'app/templates/templates.html',
		controller : 'TemplatesController',
		controllerAs : 'templates',
		activeNav: 'templates',
		siteTitle: 'Templates'
	});
}