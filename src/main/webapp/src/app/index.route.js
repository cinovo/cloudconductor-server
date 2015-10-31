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
		.when('/templates', {
			templateUrl : 'app/templates/templates.html',
			controller : 'TemplatesController',
			controllerAs : 'templates',
			activeNav: 'templates',
			siteTitle: 'Templates'
		})
		.when('/config/:templateid', {
			templateUrl : 'app/config/config.html',
			controller : 'ConfigController',
			controllerAs : 'Config',
			activeNav: 'config',
			siteTitle: 'Config'
		})
		.when('/files', {
			templateUrl : 'app/files/files.html',
			controller : 'FilesController',
			controllerAs : 'files',
			activeNav: 'files',
			siteTitle: 'Files'
		})
		.when('/sshkeys', {
			templateUrl : 'app/sshkeys/sshkeys.html',
			controller : 'SSHKeysController',
			controllerAs : 'sshkeys',
			activeNav: 'sshkeys',
			siteTitle: 'SSHKeys'
		})
		.when('/services', {
			templateUrl : 'app/services/services.html',
			controller : 'ServicesController',
			controllerAs : 'services',
			activeNav: 'services',
			siteTitle: 'Services'
		})
		.when('/settings', {
			templateUrl : 'app/settings/settings.html',
			controller : 'SettingsController',
			controllerAs : 'settings',
			activeNav: 'settings',
			siteTitle: 'Settings'
		})
		.when('/links/:linkid', {
			templateUrl : 'app/links/links.html',
			controller : 'LinksController',
			controllerAs : 'links',
			activeNav: 'links',
			siteTitle: 'Links'
		})
		.otherwise({redirectTo : '/'});
	}

})();
