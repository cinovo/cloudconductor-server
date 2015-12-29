'use strict';

angular.module('cloudconductor')

.controller('PackageserversController',[ '$scope', '$http', 'alertService', function(scope, http, alert) {
	var ctrl = this;
	ctrl.load = function() {
		http.get('/api/packageservergroup').then(function(res) {
			ctrl.list = res.data;
			ctrl.list.forEach(function(entry) {
				entry.details = [];
				var counter = 0;
			    entry.packageServers.forEach(function(psId) {
			    	http.get('/api/packageserver/'+psId).then(function(res) {
			    			entry.details[counter++] = res.data;
			    	}, function(err) {
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
		http.delete('/api/packageserver/'+serverId).then(function() {
			alert.success('The server has been successfully deleted.');
			ctrl.load();
		}, function(err) {
			alert.danger('The server couldn\'t be deleted.');
		});
	};
	
	ctrl.deleteGroup = function(groupName) {
		http.delete('/api/packageservergroup/'+groupName).then(function() {
			alert.success('The server group has been successfully deleted.');
			ctrl.load();
		}, function(err) {
			if(err.status == 409) {
				alert.danger('The server group is still used by a template.');
			}else {
				alert.danger('The server group couldn\'t be deleted.');
			}
			ctrl.load();
		});
	};
}])

.controller('PSGNewController',[ '$scope', '$http','$routeParams', 'alertService', '$rootScope', function(scope, http, routeParams, alert, rootScope) {
	var ctrl = this;
	rootScope.siteTitle = "Add a new " + rootScope.siteTitle + " Group";
}])

.controller('PSGModController',[ '$scope', '$http','$routeParams', 'alertService', '$rootScope', '$location', function(scope, http, routeParams, alert, rootScope, location) {
	var ctrl = this;
	rootScope.siteTitle = "Edit " + rootScope.siteTitle + " Group";
	
	http.get('/api/packageservergroup/'+routeParams.name).then(function(res) {
		ctrl.data = res.data;
		ctrl.data.details = [];
		var counter = 0;
		ctrl.data.packageServers.forEach(function(psId) {
		   	http.get('/api/packageserver/'+psId).then(function(res) {
		   		ctrl.data.details[counter++] = res.data;
		   	}, function(err) {
		   		console.log(err);
		   	});
		});
	}, function(err) {
		alert.info('The package server group does not exist.');
		location.path('/packageservers');
	});
	
	ctrl.save = function() {
		
	};
	
	ctrl.addServer = function() {
		ctrl.save();
		location.path('/packageservers/'+routeParams.name+'/new');
	};
	
}])
.controller('PSNewController',[ '$scope', '$http','$routeParams', 'alertService', '$rootScope', function(scope, http, routeParams, alert, rootScope) {
	var ctrl = this;
	rootScope.siteTitle = "Add a new " + rootScope.siteTitle;
}]);
