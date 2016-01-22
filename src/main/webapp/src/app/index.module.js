(function() {
    'use strict';
    
    angular.module('cloudconductor', ['ngCookies', 'ngRoute', 'ui.bootstrap', 'toggle-switch', 'angular-confirm', 'cloudconductor-client'])
    .controller('AppCtrl', AppCtrl);

    function AppCtrl($scope, $rootScope, linksClient) {
    	$scope.activeNav = 'home';
    	$scope.$on('$routeChangeStart', function(next, current) {
	    	if (current && current.$$route) {
	    		$scope.activeNav = current.$$route.activeNav;
	            if (current.$$route.siteTitle) {
	            	$rootScope.siteTitle = current.$$route.siteTitle;
	            }
	        } else {
	        	$scope.activeNav = 'home';
	        }
	    });
    	
    	linksClient.getLinks().then(function(res) {
			$scope.links = res.data;
		});
    }
    
})();
