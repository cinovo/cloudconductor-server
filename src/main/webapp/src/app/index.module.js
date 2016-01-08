'use strict';

var ccModule = angular.module('cloudconductor', ['ngCookies', 'ngRoute', 'ui.bootstrap', 'toggle-switch', 'angular-confirm', 'cloudconductor-client']);

ccModule.controller('AppCtrl', ['$scope', '$rootScope', '$route', '$http', function(scope, rootScope, route, http) {
	scope.activeNav = 'home';
    scope.$on('$routeChangeStart', function(next, current) {
    	if (current && current.$$route) {
    		scope.activeNav = current.$$route.activeNav;
            if (current.$$route.siteTitle) {
            	rootScope.siteTitle = current.$$route.siteTitle;
            }
        } else {
            scope.activeNav = 'home';
        }
    });
	http.get('/api/links').then(function(res) {
		scope.links = res.data;
	});
}]);
