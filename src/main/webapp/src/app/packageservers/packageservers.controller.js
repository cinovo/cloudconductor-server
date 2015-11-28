'use strict';

angular.module('cloudconductor')
.controller('PackageserversController',[ '$scope', '$http', 'alertService', function(scope, http, alert) {
	var ctrl = this;
	
	ctrl.load = function() {
		http.get('/api/packageservergroup').then(function(res) {
			ctrl.list = res.data;
			ctrl.list.forEach(function(entry) {
				entry.details = new Array();
				var counter = 0;
			    entry.packageServers.forEach(function(psId) {
			    	http.get('/api/packageserver/'+psId).then(function(res) {
			    			entry.details[counter++] = res.data;
			    	}, function(reason) {
			    		console.log(err);
			    	});
			    });
			});
		
		}, function(err) {
			console.log(err);
		});
	};
	ctrl.load();

	ctrl.deleteServer = function(serverId) {
		http.delete('/api/packageserver/'+serverId).then(function(res) {
			alert.success('The server has been successfully deleted.');
			ctrl.load();
		}, function(reason) {
			console.log(err);
		});
	};
	
}]);
