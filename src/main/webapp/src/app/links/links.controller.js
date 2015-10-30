(function() {
	'use strict';

	angular.module('cloudconductor')
	.controller('LinksController',  ['$scope', '$routeParams', function($scope, $routeParams) {
	    $scope.linkid = $routeParams.linkid;
	}]);

})();
