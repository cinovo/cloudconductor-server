(function() {
	'use strict';
	
	angular.module('cloudconductor-client').factory('packageClient', PackageClient);
	
	/* @ngInject */
	function PackageClient($http, ccClientConfig, httpErrors) {

		return {
			getPackages: function() {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/packages').catch(httpErrors.error);
			},
			getPackageDetails: function(packageName) {
				return $http.get(ccClientConfig.getBaseUrl() + '/api/packages/' + packageName).catch(httpErrors.error);
			},
			deletePackage: function(packageName) {
				return $http.delete(ccClientConfig.getBaseUrl() + '/api/packages/' + packageName).catch(httpErrors.error);
			}
		};
	}
	

})();

