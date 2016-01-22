(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('configClient', ConfigClient);
	
	/* @ngInject */
	function ConfigClient($http, ccClientConfig, httpErrors) {
		
		return {
			getConfig: function(templateName) {
				return $http.get(ccClientConfig.getBaseUrl() +'/api/config/'+templateName).catch(httpErrors.error);
			},
			saveChanges: function(templateName, changes) {
				var req = {
						method: 'POST',
		                url: ccClientConfig.getBaseUrl() + '/api/config/' + templateName,
		                headers: { 'Content-Type': 'application/json' },
		                data: changes
				};
		        return $http(req).catch(httpErrors.error);
			}
		}
	}
	
})();