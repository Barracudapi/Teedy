'use strict';

/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUser', function ($scope, $state, Restangular) {

    // Load users from the server
    $scope.loadUsers = function () {
        Restangular.one('user/list').get({
            sort_column: 1,
            asc: true
        }).then(function (data) {
            $scope.users = data.users;
        });
    };

    $scope.loadUsers();

    // Edit a user
    $scope.editUser = function (user) {
        $state.go('settings.user.edit', {username: user.username});
    };

    // Get all registration requests (admin only)
    $scope.registrations = [];
    $scope.loadRegistrations = function () {
        Restangular.one('registration/list').get().then(function (data) {
            $scope.registrations = data.registrations || [];
        });
    };

    if ($scope.isAdmin) {
        $scope.loadRegistrations();
    }

    // Approve registration request
    $scope.approveRegistration = function (req) {
        Restangular.one('registration').post('status/update', JSON.stringify({
            id: req.id,
            status: 'APPROVED'
        }), undefined, {'Content-Type': 'application/json;charset=utf-8'}).then(function () {
            $scope.loadUsers();
            $scope.loadRegistrations();
        });
    };

    // Reject registration request
    $scope.rejectRegistration = function (req) {
        Restangular.one('registration').post('status/update', JSON.stringify({
            id: req.id,
            status: 'REJECTED'
        }), undefined, {'Content-Type': 'application/json;charset=utf-8'}).then(function () {
            $scope.loadRegistrations();
        });
    };
});