(function() {
	'use strict';

	angular.module('cloudconductor').controller('PSGModController', PSGModController);

	/* @ngInject */
	function PSGModController($rootScope, $location, alertService, $routeParams, $http, packageserverService) {
		// vm stands for ViewModel
		var vm = this;
		vm.data = {};
		vm.save = save;
		vm.addServer = addServer;
		vm.editServer = editServer;
		vm.deleteServer = deleteServer;
		vm.setPrimary = setPrimary;
		
		if($routeParams.groupId) {
			$rootScope.siteTitle = 'Edit ' + $rootScope.siteTitle + ' Group';
			load(); 
		}else {
			$rootScope.siteTitle = 'Add a new ' + $rootScope.siteTitle + ' Group';
			prepare();
		}
		
		function load() {
        	packageserverService.getGroup($routeParams.groupId).then(function (res) {
        		vm.data = res.data;
        		console.log(res.data)
    	   });
        }
		
		function prepare() {
			vm.data = {
				'@class': 'de.cinovo.cloudconductor.api.model.PackageServerGroup',
				packageServers: [],
				name: '',
				primaryServer: 0
			}
		}
		
		function save(modServer, redirectAddition) {
			var redirectTo = '/packageservers/';
			if (typeof redirectAddition === 'undefined' || redirectAddition === null ) {redirectAddition = '';}
			if (typeof modServer === 'undefined' || modServer === null ) {modServer = false;}
			if(fieldsContainError()) { return; }
			
			var http;
			if($routeParams.groupId) {
				http = packageserverService.editGroup(vm.data);
			}else {
				http = packageserverService.newGroup(vm.data);
			}
			http.then(function (res) {
				if(modServer && !vm.data.id) {
					vm.data.id = res.data;
				}
				if(!modServer) {
					alertService.success('The server group '+ vm.data.name + ' has been saved.');
					$location.path(redirectTo);
				}else {
					$location.path(redirectTo + vm.data.id + redirectAddition).search('modType=1');
				}
			}, function(err) {
				alertService.danger('The server group '+ vm.data.name + ' already exists.');
            });
		}

		function addServer() {
			save(true, '/new');
		}

		function editServer(serverId) {
			save(true, '/edit/'+serverId);
		}
		
		function deleteServer(serverId) {
			packageserverService.deleteServer(serverId).then(function() {
				vm.data.packageServers.forEach(function(item, index, object) {
					if(item.id === serverId) {
						object.splice(index,1);
					}
				});
			});
		}
		
		function setPrimary(serverId) {
			vm.data.primaryServer = serverId;
		}
		
		function fieldsContainError() {
			var error = false;
			if(! vm.data.name) {
				alertService.danger("Please insert a group name.");
				error = true;
			}
			return error;
		}
	}

})()

