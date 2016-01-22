(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('linksClient', LinksClient);
	
	/* @ngInject */
	function LinksClient($http, ccClientConfig, httpErrors) {

		return {
			getLinks: function() {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/links').catch(httpErrors.error);
			}
		};
	}
	

})();

