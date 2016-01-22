(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('sshKeysClient', SSHKeysClient);
	
	/* @ngInject */
	function SSHKeysClient($http, ccClientConfig, httpErrors) {

		return {
			getKeys: function() {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/sshkeys').catch(httpErrors.error);
			},
			getKeysTemplate: function(templateName) {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/templates/'+ templateName +'/sshkeys').catch(httpErrors.error);
			}
		};
	}
	

})();