(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('templateClient', TemplateClient);
	
	/* @ngInject */
	function TemplateClient($http, ccClientConfig, httpErrors) {

		return {
			getTemplates: function() {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/templates').catch(httpErrors.error);
			}
		};
	}
	

})();