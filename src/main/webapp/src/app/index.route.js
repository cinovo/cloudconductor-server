(function() {
	'use strict';

	angular.module('cloudconductor').config(routeConfig);
	
	function routeConfig($routeProvider) {
		$routeProvider
		.when('/', {
			templateUrl : 'app/main/main.html',
			controller : 'MainController',
			controllerAs : 'main',
			activeNav: 'home',
			siteTitle: 'Dashboard'
		})
		.when('/hosts', {
			templateUrl : 'app/hosts/hosts.html',
			controller : 'HostsController',
			controllerAs : 'hosts',
			activeNav: 'hosts',
			siteTitle: 'Hosts'
		})
		.when('/files', {
			templateUrl : 'app/files/files.html',
			controller : 'FilesController',
			controllerAs : 'files',
			activeNav: 'files',
			siteTitle: 'Files'
		})
		.when('/settings', {
			templateUrl : 'app/settings/settings.html',
			controller : 'SettingsController',
			controllerAs : 'settings',
			activeNav: 'settings',
			siteTitle: 'Settings'
		})
		.otherwise({redirectTo : '/'});
	}

})();