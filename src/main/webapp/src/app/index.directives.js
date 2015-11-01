'use strict';

angular.module('cloudconductor')
.directive('ccPanelhead', function() {
	return {
		restrict : 'E',
		templateUrl : 'app/directives/panelhead.directive.html',
		transclude : true,
		replace: true,
		scope : {
			icon : "@",
			title : "@"
		},
		link: function(scope, el, attrs, ctrl, transcludeFn) {
            transcludeFn( scope, function( clone ) {
            	scope.showTransclude = clone.text().trim().length ? true : false;
            });
        }
	}
});
