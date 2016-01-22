(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('serviceClient', ServiceClient);
	
	/* @ngInject */
	function ServiceClient($http, ccClientConfig, httpErrors) {

		return {
			getServices: function() {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/services').catch(httpErrors.error);
			},
			getServiceDetails: function(serviceName) {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/services/' + serviceName).catch(httpErrors.error);
			}
		};
	}
	

})();