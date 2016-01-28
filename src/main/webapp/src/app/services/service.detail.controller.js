(function() {
    'use strict';

    angular.module('cloudconductor').controller('ServiceDetailController', ServiceDetailController);
    
    /* @ngInject */
    function ServiceDetailController(serviceClient, $rootScope, $routeParams, packageClient, alertService, $location) {
    	var vm = this;
    	vm.load = load;
    	vm.save = save;
    	vm.deleteService = deleteService;
    	vm.autoFill = autoFill;
    	
    	if ($routeParams.modType == '1' && $routeParams.detail) { vm.redirectTo = '/packages/'+$routeParams.detail}
    	else if ($routeParams.modType == '1') {vm.redirectTo = '/packages/'}
		else  { vm.redirectTo = '/services/' }
    	
    	
    	vm.load();
    	
    	function load() {
    		if($routeParams.serviceName === 'new') {
    			prepare();
    		} else {
    			serviceClient.getServiceDetails($routeParams.serviceName).then(function(res) {
					vm.service = res.data;
					$rootScope.siteTitle = 'Service Detail - ' + vm.service.name;
	    		}, function(err) {
	    			prepare();
	    			if($routeParams.serviceName) {
	    				vm.service.packages =[$routeParams.serviceName];
	    				vm.autoFill($routeParams.serviceName);
	    			}
	    		});
    		}
    		
    		packageClient.getPackages().then(function(res) {
    			vm.packages = res.data;
    			if(vm.service && vm.service.packages) {
	    			vm.packages.forEach(function(item, index, object) {
	    				item.selected = false;
	    				if($.inArray(item.name, vm.service.packages) > -1) {
	    					item.selected = true;
	    				}
	    			});
    			}
    			vm.packages.sort(function(a, b) { 
    				return a.name.localeCompare(b.name);
    			});
    		});
    		
    		
    	}
    	
    	function autoFill(value) {
    		console.log(value)
    		if(!vm.service.name) {
    			vm.service.name = value;
    		}
    		if(!vm.service.initScript) {
    			vm.service.initScript = value;
    		}
    		if(!vm.service.description) {
    			vm.service.description = value;
    		}
    	}
    	
    	function save() {
    		vm.service.packages = [];
    		vm.packages.forEach(function(item, index, object) {
				if(item.selected === true) {
					vm.service.packages.push(item.name);
				}
			});
    		
    		
    		if(fieldsContainError()) { return; }
    		
    		serviceClient.saveService(vm.service).then(function(res) {
    			alertService.success("The service has been saved.");
    		});
    	}
    	
    	function deleteService() {
    		serviceClient.deleteService(vm.service.name).then(function(res) {
    			alertService.success("The service has been deleted.");
    			$location.path(vm.redirectTo);
    		});
    	}
    	
    	function prepare() {
			vm.service = {
				'@class': 'de.cinovo.cloudconductor.api.model.Service',
				id: -1,
				state: 'STOPPED',
				packages: []
			}
			$rootScope.siteTitle = 'Add a new service';
		}
    	
    	function fieldsContainError() {
			var error = false;
			if(! vm.service.name) {
				alertService.danger("Please insert a name.");
				error = true;
			}
			if(! vm.service.initScript) {
				alertService.danger("Please insert an init-script.");
				error = true;
			}
			return error;
		}
    }
})();