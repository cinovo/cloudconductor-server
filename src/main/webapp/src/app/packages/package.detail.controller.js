(function() {
    'use strict';

    angular.module('cloudconductor').controller('PackageDetailController', PackageDetailController);
    
    /* @ngInject */
    function PackageDetailController(serviceClient, $rootScope, $routeParams, packageClient, alertService, $location) {
    	var vm = this;
    	vm.services = [];

    	vm.load = load;
    	vm.deletePackage = deletePackage;
    	vm.addService = addService;
    	
    	vm.load();
    	
    	function load() {
    		if($routeParams.serviceName === 'new') {
    			prepare();
    		} else {
    			packageClient.getPackageDetails($routeParams.packageName).then(function(res) {
					vm.data = res.data;
					$rootScope.siteTitle = 'Service Detail - ' + vm.data.name;
	    		});
    		}
    		
    		serviceClient.getServices().then(function(res) {
    			res.data.forEach(function(item) {
    				if(res.data && vm.data.name) {
	    				if($.inArray(vm.data.name, item.packages) > -1) {
	    					vm.services.push(item);
	    				}
    				}
    			});
    			
    			vm.services.sort(function(a, b) { 
    				return a.name.localeCompare(b.name);
    			});
    		});
    	}
    	
    	function deletePackage() {
    		packageClient.deletePackage(vm.data.name).then(function(res) {
    			alertService.success("The package has been deleted.");
    			$location.path("/packages/");
    		});
    	}
    	
    	function prepare() {
			vm.data = {
				'@class': 'de.cinovo.cloudconductor.api.model.Service',
				id: -1,
				state: 'STOPPED',
				packages: []
			}
			$rootScope.siteTitle = 'Add a new service';
		}
    	
    	function addService() {
    		$location.path("/service/"+vm.data.name).search('modType=1&detail='+vm.data.name);
    	}
    }
})();