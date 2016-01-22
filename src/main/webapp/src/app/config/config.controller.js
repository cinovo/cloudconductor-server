(function() {
    'use strict';

	angular.module('cloudconductor').controller('ConfigController', ConfigController);
	
	/* @ngInject */
	function ConfigController($scope, $http, alertService, $routeParams, configClient) {
	    var vm = this;
	    vm.load = load;
	    vm.saveChanges = saveChanges;
	    vm.addKey = addKey;
	    vm.deleteKey = deleteKey;
	    	
	    load();
	    	
	    function load() {
	    	configClient.getConfig($routeParams.templateid).then(function(res) {
	    		console.log(res);
	    		vm.configValues = res.data;
	        });
	    }
	
	
	    function saveChanges() {
	        // format to JSON
	    	// http.put('/api/config/' + ctrl.templateName, ctrl.configValues).then(function(res) {
	    	configClient.saveChanges('GLOBAL', vm.configValues).then(function(res) {
	    		alertService.success('The configuration settings for template '+ $routeParams.templateid +' has been saved successfully.');
	        }, function(err) {
	        	alertService.error('Something went wrong when trying to save the configuration for template '+ $routeParams.templateid +'!');
	        });
	    }
	
	    // add a key to the variable array. Use saveChanges() to save the changes
	   function addKey(key, value) {
	    	vm.configValues[key] = value;
	    }
	
	    // delete from settings array; Use saveChanges() to save the changes
	   function deleteKey( key ) {
	        delete vm.configValues[key];
	   }
	}

})();
