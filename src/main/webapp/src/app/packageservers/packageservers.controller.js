(function() {
    'use strict';

    angular.module('cloudconductor').controller('PackageserversController', PackageserversController);

    /* @ngInject */
    function PackageserversController($http, alertService) {
        var vm = this;
        vm.load = load;
        vm.deleteServer = deleteServer;
        vm.deleteGroup = deleteGroup;

        load();

        function load() {
            $http.get('/api/packageservergroup').then(function(res) {
                vm.list = res.data;
                vm.list.forEach(function(entry) {
                    entry.details = [];
                    var counter = 0;
                    entry.packageServers.forEach(function(psId) {
                        $http.get('/api/packageserver/' + psId).then(function(res) {
                            entry.details[counter++] = res.data;
                        }, function(err) {
                            console.log(err);
                        });
                    });
                });

            }, function(err) {
                console.log(err);
            });
        }

        function deleteServer(serverId) {
            $http.delete('/api/packageserver/' + serverId).then(function() {
                alertService.success('The server has been successfully deleted.');
                load();
            }, function(err) {
                alertService.danger('The server couldn\'t be deleted.');
            });
        }

        function deleteGroup(groupName) {
            $http.delete('/api/packageservergroup/' + groupName).then(function() {
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