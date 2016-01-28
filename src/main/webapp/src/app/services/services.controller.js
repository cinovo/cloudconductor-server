(function() {
	'use strict';
    
    angular.module('cloudconductor').controller('ServicesController',ServicesController);
    
    function ServicesController(serviceClient, alertService) {
    	var vm = this;
    	vm.load = load;
    	vm.deleteService = deleteService;
    	
    	vm.load();
    	
    	function load() {
    		serviceClient.getServices().then(function(res) {
				vm.list = res.data;
    		});
    	}
    	
    	function deleteService(service) {
    		serviceClient.deleteService(service.name).then(function(res) {
    			alertService.success("The service has been deleted.");
    			vm.load();
    		});
    	}
    }
    
})();
