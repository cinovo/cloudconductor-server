(function() {
	'use strict';

	angular.module('cloudconductor')
	.controller('ConfigController', ['$scope', '$routeParams', function($scope, $routeParams) {
	    $scope.templateid = $routeParams.templateid;
	}]);

})();
