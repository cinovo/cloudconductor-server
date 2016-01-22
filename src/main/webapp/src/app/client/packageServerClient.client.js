(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('packageserverClient', PackageserverClient);
	
	/* @ngInject */
	function PackageserverClient($http, ccClientConfig, httpErrors) {

		return {
			getGroups: function() {
				return $http.get(ccClientConfig.getBaseUrl() +'/api/packageservergroup').catch(httpErrors.error);
			},
			getGroup: function(groupId) {
				return $http.get(ccClientConfig.getBaseUrl() +'/api/packageservergroup/'+groupId).catch(httpErrors.error);
			},
	        deleteGroup : function deleteGroup(groupName) {
	        	return $http.delete(ccClientConfig.getBaseUrl() + '/api/packageservergroup/' + groupName).catch(httpErrors.error);
		    },
		    newGroup : function (group) {
	            var req = {
	            		method: 'POST',
	                    url: ccClientConfig.getBaseUrl() + '/api/packageservergroup/',
	                    headers: { 'Content-Type': 'application/json' },
	                    data: group
	            };
	            return $http(req).catch(httpErrors.error);
	       },
		   editGroup : function (group) {
	           var req = {
	                   method: 'PUT',
	                   url: ccClientConfig.getBaseUrl() + '/api/packageservergroup/',
	                   headers: { 'Content-Type': 'application/json' },
	                   data: group
	           };
	           return $http(req).catch(httpErrors.error);
	       },
	       getServer: function (serverId) {
	    	   return $http.get(ccClientConfig.getBaseUrl() + '/api/packageserver/' + serverId).catch(httpErrors.error);
	       },
	       deleteServer : function deleteServer(serverId) {
	           return $http.delete(ccClientConfig.getBaseUrl() + '/api/packageserver/' + serverId).catch(httpErrors.error);
	       },
	       newServer : function (server) {
	    	   var req = {
	                   method: 'POST',
	                   url: ccClientConfig.getBaseUrl() + '/api/packageserver/',
	                   headers: { 'Content-Type': 'application/json' },
	                   data: server
	           };
	           return $http(req).catch(httpErrors.error);
	       }, 
	       editServer : function (server) {
	    	   var req = {
	                   method: 'PUT',
	                   url: ccClientConfig.getBaseUrl() + '/api/packageserver/',
	                   headers: { 'Content-Type': 'application/json' },
	                   data: server
	           };
	           return $http(req).catch(httpErrors.error);
	       }
		};
	}
	

})();