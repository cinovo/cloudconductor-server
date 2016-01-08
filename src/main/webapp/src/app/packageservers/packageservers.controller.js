(function() {
    'use strict';

    angular.module('cloudconductor').controller('PackageserversController', PackageserversController);

    /* @ngInject */
    function PackageserversController($http, alertService, packageserverService) {
        var vm = this;
        vm.load = load;
        vm.deleteServer = deleteServer;
        vm.deleteGroup = deleteGroup;

        load();
       
        function load() {
        	packageserverService.getGroups().then(function (res) {
        		vm.data = res.data;
    	   });
        }

        function deleteServer(serverId) {
        	packageserverService.deleteServer(serverId).then(function(res) {
                alertService.success('The server has been successfully deleted.');
                load();
            });
        }

        function deleteGroup(groupId) {
        	packageserverService.deleteGroup(groupId).then(function(res) {
                alertService.success('The server group has been successfully deleted.');
                load();
            }, function(err) {
                if (err.status == 409) {
                    alertService.danger('The server group is still used by a template.');
                } else {
                    alertService.danger('The server group couldn\'t be deleted.');
                }
                load();
            });
        }
        
    }

})()