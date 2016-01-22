(function() {
	'use strict';

	angular.module('cloudconductor').config(SSHKEYRouteConfig);
			
	function SSHKEYRouteConfig($routeProvider) {
		$routeProvider
		.when('/sshkeys', {
			templateUrl : 'app/sshkeys/sshkeys.html',
			controller : 'SSHKeysController',
			controllerAs : 'sshkeys',
			activeNav: 'sshkeys',
			siteTitle: 'SSHKeys'
		});
	}

})();
