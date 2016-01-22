(function() {
    'use strict';

angular.module('cloudconductor').controller('PackagesController', PackagesController);

	function PackagesController(packageClient) {
		var vm = this;
		vm.load = load;
		
		load();
	    
	    function load() {
	    	packageClient.getPackages().then(function (res) {
	    		vm.list = res.data;
		   });
	    }
	}

})();
