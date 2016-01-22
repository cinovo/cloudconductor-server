(function() {
	'use strict';
	angular.module('cloudconductor-client', [])
	.provider('ccClientConfig', CCClientConfig)	
	.factory('httpErrors', HTTPErrors)
	
	
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
	
})();