(function() {
    'use strict';

    angular.module('cloudconductor').controller('SSHKeysController', SSHKeysController);

    /* @ngInject */
    function SSHKeysController($scope, alertService, sshKeysClient, templateClient) {
		var vm = this;
		vm.selectedTemplate = '';
		
		vm.load = load;
		vm.loadTemplates = loadTemplates;
		
		load();
		loadTemplates();
		
		$scope.$watch(function(){return vm.selectedTemplate}, load);
		
		function load() {
			if(vm.selectedTemplate){
				sshKeysClient.getKeysTemplate(vm.selectedTemplate.name).then(function(res) {
					vm.list = res.data;
				});
			} else {
				sshKeysClient.getKeys().then(function(res) {
					vm.list = res.data;
				});
			}
		}
		
		function loadTemplates() {
			templateClient.getTemplates().then(function(res) {
				vm.templates = res.data; 
			});
		}
	}

})();