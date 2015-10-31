	'use strict';

	angular.module('cloudconductor')
	.controller('SSHKeysController', [ '$scope', '$http', function(scope, http) {
		var ctrl = this;
		http.get('/api/templates').then(function(res) {
			ctrl.templates = res.data;
		}, function(err) {
			console.log(err);
		});
		
		scope.selectedTemplate = '';
		scope.$watch('selectedTemplate', function(){
			if(scope.selectedTemplate){
			http.get('/api/templates/'+ scope.selectedTemplate.name +'/sshkeys').then(function(res) {
				ctrl.list = res.data;
			}, function(err) {
				console.log(err);
			});
			} else {
				http.get('/api/sshkeys').then(function(res) {
					ctrl.list = res.data;
				}, function(err) {
					console.log(err);
				});
			}
			
		});
	}]);