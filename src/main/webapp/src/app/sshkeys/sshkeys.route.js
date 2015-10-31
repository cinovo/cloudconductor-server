'use strict';

angular.module('cloudconductor').config(routeConfig);

function routeConfig($routeProvider) {
	$routeProvider
	.when('/sshkeys', {
		templateUrl : 'app/sshkeys/sshkeys.html',
		controller : 'SSHKeysController',
		controllerAs : 'sshkeys',
		activeNav: 'sshkeys',
		siteTitle: 'SSHKeys'
	});
}