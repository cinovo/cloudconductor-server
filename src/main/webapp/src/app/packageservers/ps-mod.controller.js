(function() {
	'use strict';

	angular.module('cloudconductor').controller('PSModController', PSModController);

	/* @ngInject */
	function PSModController($rootScope, $location, alertService, $routeParams, $http, packageserverService) {
		var vm = this;
		vm.data = {};
		vm.addData = {};
		vm.save = save;
		
		if ($routeParams.modType == '1') { vm.redirectTo = '/packageservers/edit/'+$routeParams.groupId; }
		else  { vm.redirectTo = '/packageservers/' }
		
		if($routeParams.serverId) {
			$rootScope.siteTitle = 'Edit ' + $rootScope.siteTitle;
			load(); 
		}else {
			$rootScope.siteTitle = 'Add a new ' + $rootScope.siteTitle;
			prepare();
		}
		
		function load() {
        	packageserverService.getServer($routeParams.serverId).then(function (res) {
        		vm.data = res.data;
        	});
        	packageserverService.getGroup($routeParams.groupId).then(function(res) {
    			vm.addData.serverGroupName = res.data.name;
    		});
        }
		
		function prepare() {
			vm.data = {
				indexerType: 'NONE',
				providerType: 'NONE',
				serverGroup: $routeParams.groupId
			}
			packageserverService.getGroup($routeParams.groupId).then(function(res) {
    			vm.addData.serverGroupName = res.data.name;
    		});
		}
		
		function save() {
			if(fieldsContainErrors()) { return; }
			
			var http;
			if($routeParams.serverId) {
				http = packageserverService.editServer(vm.data);
			}else {
				http = packageserverService.newServer(vm.data);
			}
			
			http.then(function (res) {
				alertService.success('The package server '+ vm.data.description + ' has bee saved.');
				$location.path(vm.redirectTo);
			}, function(err) {
				alertService.danger('The package server '+ vm.data.description + ' couldn\'t be saved.');
            });
		}
		
		function fieldsContainErrors() {
			var error = false;
			if(! vm.data.description) {
				alertService.danger("Please insert a description.");
				error = true;
			}
			if(! vm.data.path) {
				alertService.danger("Please insert a path.");
				error = true;
			}
			return error;
		}
	}

})()

