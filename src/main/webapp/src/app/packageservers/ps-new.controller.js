(function() {
	'use strict';

	angular.module('cloudconductor').controller('PSNewController', PSNewController);

	/* @ngInject */
	function PSNewController($rootScope) {
		$rootScope.siteTitle = 'Add a new ' + $rootScope.siteTitle;
	}

})()

