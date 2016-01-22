(function() {
    'use strict';

    angular.module('cloudconductor').controller('ServiceDetailController', ServiceDetailController);
    
    /* @ngInject */
    function ServiceDetailController(serviceClient, $rootScope, $routeParams) {
    	var vm = this;
    	vm.load = load;
    	vm.save = save;
    	
    	load();
    	
    	function load() {
    		serviceClient.getServiceDetails($routeParams.serviceName).then(function(res) {
				vm.service = res.data;
				$rootScope.siteTitle = 'Service Detail - ' + vm.service.name;
				console.log(vm.service)
    		});
    	}
    	
    	function save() {
			console.log('Save', vm.service);
    	}
    }
})();