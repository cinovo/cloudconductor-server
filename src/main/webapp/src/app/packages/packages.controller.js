(function() {
    'use strict';

angular.module('cloudconductor').controller('PackagesController', PackagesController);

	function PackagesController(packageClient, alertService) {
		var vm = this;
		vm.load = load;
		vm.deletePackage = deletePackage;
		
		vm.load();
	    
	    function load() {
	    	packageClient.getPackages().then(function (res) {
	    		vm.list = res.data;
		   });
	    }
	    
	    function deletePackage(packageName) {
    		packageClient.deletePackage(packageName).then(function(res) {
    			alertService.success("The package has been deleted.");
    			load();
    		});
    	}
	}

})();
