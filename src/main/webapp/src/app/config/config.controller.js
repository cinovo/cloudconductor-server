'use strict';

angular.module('cloudconductor')
.controller('ConfigController', [ '$scope', '$http', '$template', 'alertService', '$log', function(scope, http, template, alert, log) {
    var ctrl = this;

    log.info(template.name);
    ctrl.templateName = template;

    ctrl.load = function() {
        http.get('/api/config/123').then(function(res) {
        //  console.log(res);
            ctrl.configValues = angular.fromJson(res.data);
        }, function(err) {
            console.log(err);
        });
    };

    ctrl.load();

    ctrl.saveChanges = function () {
        log.info(ctrl.configValues);
        // format to JSON
//      http.put('/api/config/' + ctrl.templateName, ctrl.configValues).then(function(res) {
        http.put('/api/config/GLOBAL', ctrl.configValues).then(function(res) {
            alert.success('The configuration settings for template '+ templateId +' has been saved successfully.');
        }, function(err) {
            alert.error('Something went wrong when trying to save the configuration for template '+ templateId +'!');
            console.log(err);
        });
    }

    // add a key to the variable array. Use saveChanges() to save the changes
    ctrl.addKey = function(key, value) {
        ctrl.configValues[key] = value;
    }

    // delete from settings array; Use saveChanges() to save the changes
    ctrl.deleteKey = function ( key ) {
        log.info(key);
        delete ctrl.configValues[key]
    };
}]);
