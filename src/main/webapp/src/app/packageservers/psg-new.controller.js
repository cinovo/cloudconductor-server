(function() {
	'use strict';

	angular.module('cloudconductor').controller('PSGNewController', PSGNewController);

	/* @ngInject */
	function PSGNewController($rootScope) {
		rootScope.siteTitle = 'Add a new ' + rootScope.siteTitle + ' Group';
	}

})()