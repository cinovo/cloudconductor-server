//(function() {
//	'use strict';
//
//	angular.module('cloudconductor')
//	.controller('ConfigController', ['$scope', '$routeParams', function($scope, $routeParams) {
//	    $scope.templateid = $routeParams.templateid;
//	}]);
//
//})();
'use strict';

angular.module('cloudconductor')
.controller('ConfigController', [ '$scope', '$http', function(scope, http) {
	var ctrl = this;
	http.get('/api/config/').then(function(res) {
		console.log(res);
		ctrl.list = res.data;
	}, function(err) {
		console.log(err);
	});
}]);
