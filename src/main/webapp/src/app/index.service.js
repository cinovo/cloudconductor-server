'use strict';

angular.module('cloudconductor')
.factory('alertService', ['$rootScope', function(rootScope){
	rootScope.alerts = [];

	rootScope.closeAlert = function(index) {
		rootScope.alerts.splice(index, 1);
    };
    function addAlert(type, msg) {
    	rootScope.alerts.push({
    		'type': type,
    		'msg': msg
    	});		
	}
	return {
		success: function (message) {
			addAlert("success", message);
		},
		info: function (message) {
			addAlert("info", message);
		},
		warning: function (message) {
			addAlert("warning", message);
		},
		danger: function (message) {
			addAlert("danger", message);
		}
	};
}])