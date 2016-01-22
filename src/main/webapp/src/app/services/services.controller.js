(function() {
	'use strict';
    
    angular.module('cloudconductor').controller('ServicesController',ServicesController);
    
    function ServicesController(serviceClient) {
    	var vm = this;
    	vm.load = load;
    	
    	load();
    	
    	function load() {
    		serviceClient.getServices().then(function(res) {
				vm.list = res.data;
    		});
    	}
    }
    
})();
