(function() {
	'use strict';

	angular.module('cloudconductor').controller('PSGModController', PSGModController);

	/* @ngInject */
	function PSGModController($rootScope, $location, alertService, $routeParams, $http) {
		// vm stands for ViewModel
		var vm = this;
		vm.data = {};
		vm.save = save;
		vm.addServer = addServer;
		
		$rootScope.siteTitle = 'Edit ' + $rootScope.siteTitle + ' Group';

		// should go into PackageServerGroupService
		$http.get('/api/packageservergroup/' + $routeParams.name).then(function(res) {
			vm.data = res.data;
			vm.data.details = [];
			var counter = 0;
			vm.data.packageServers.forEach(function(psId) {
				$http.get('/api/packageserver/' + psId).then(function(res) {
					vm.data.details[counter++] = res.data;
				}, function(err) {
					console.log(err);
				});
			});
		}, function(err) {
			alertService.info('The package server group does not exist.');
			$location.path('/packageservers');
		});

		function save() {

		};

		function addServer() {
			save();
			$location.path('/packageservers/' + $routeParams.name + '/new');
		};

	}

})()

