'use strict';

angular.module('cloudconductor')
.controller('LinksController',  ['$http', function(http) {
	var that = this;
	http.get('/api/links').then(function(res) {
		that.links = res.data;
	});
}])
.controller('LinkeditController',  ['$http', '$routeParams', '$location', function(http, routeParams, location) {
	var that = this;
	var linkid = routeParams.linkid;
	if (linkid !== 'new') {
		http.get('/api/links/' + linkid).then(function(res) {
			that.link = res.data;
		})		
	} else {
		that.link = {label:'', url:''};
	}
	this.save = function() {
		if (that.link.id) {
			// save
			http.put('/api/links/' + linkid, {data: that.link}).then(function(res) {
				console.log('Saved: ' + JSON.toString(res));
				location.path('/links/' + linkid);
			});
		} else {
			// create
			http.post('/api/links/' + linkid, {data: that.link}).then(function(res) {
				console.log('Created: ' + JSON.toString(res));
				location.path('/links/' + res.data.id);
			});
		}
		http.put()
	}
	
}]);
