'use strict';

angular.module('cloudconductor').config(routeConfig);

function routeConfig($routeProvider) {
	$routeProvider
	.when('/links', {
		templateUrl : 'app/links/links.html',
		controller : 'LinksController',
		controllerAs : 'ctrl',
		activeNav: 'links',
		siteTitle: 'Links'
	})
	.when('/links/:linkid', {
		templateUrl : 'app/links/linkEdit.html',
		controller : 'LinkEditController',
		controllerAs : 'ctrl',
		activeNav: 'links',
		siteTitle: 'Links'
	});
}