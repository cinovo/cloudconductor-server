'use strict';

var ccModule = angular.module('cloudconductor', ['ngCookies', 'ngRoute', 'ui.bootstrap', 'toggle-switch']);

ccModule.controller('AppCtrl', ['$scope', '$rootScope', '$route', function(scope, rootScope, route) {
	scope.activeNav = 'home';
    scope.$on('$routeChangeStart', function(next, current) {
    	if (current && current.$$route) {
    		scope.activeNav = current.$$route.activeNav;
            if (current.$$route.siteTitle) {
            	rootScope.siteTitle = current.$$route.siteTitle;
            };
        } else {
            scope.activeNav = 'home';
        }
    });
}]);
