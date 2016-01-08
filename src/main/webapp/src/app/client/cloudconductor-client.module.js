(function() {
	'use strict';
	angular.module('cloudconductor-client', [])
	.provider('ccClientConfig', CCClientConfig)
	.factory('httpErrors', HTTPErrors)
	.factory('packageserverService', PackageserverService);
	
	/* @ngInject */
	function CCClientConfig() {
		var baseUrl = "";
	    return {
	        setBaseUrl: function(url) {
	            console.log('Setting base url: ' + url);
	            baseUrl = url;
	        },
	        $get: function () {
	            return {
	                getBaseUrl: function() {
	                    return baseUrl;
	                }                
	            }
	        }
	    }
	}
	
	/* @ngInject */
	function HTTPErrors($q) {
		return {
			error: function(err) {
				console.log(err);
				return $q.reject(err);
			}
		}
	}
	
	/* @ngInject */
	function PackageserverService($http, ccClientConfig, httpErrors) {

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

})()