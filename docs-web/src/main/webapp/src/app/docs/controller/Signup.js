'use strict';

/**
 * AI-generated-content
 * tool: Grok
 * version: 3
 * usage: I asked the AI model to fix my /registration/create API endpoint. I copied the code and learned from it.
 */
/**
 *
 */
/**
 * Signup controller.
 */
angular.module('docs').controller('Signup', function (Restangular, $scope, $rootScope, $state, $stateParams, $dialog, User, $translate) {
    $scope.codeRequired = false;

    // Get the app configuration
    Restangular.one('app').get().then(function (data) {
        $rootScope.app = data;
    });

    // Redirect user to login page
    $scope.goToLogin = function () {
        $state.go('login');
    };

    // Register an account
    $scope.registerAccount = function () {
        // Initialize error messages
        $scope.errorMessage = '';

        // Validate inputs
        if (!$scope.user || !$scope.user.username || $scope.user.username.trim() === '') {
            $scope.errorMessage = $translate.instant('Username is required');
            return;
        }
        if (!$scope.user.password || $scope.user.password.trim() === '') {
            $scope.errorMessage = $translate.instant('Password is required');
            return;
        }
        if ($scope.user.username.length < 3) {
            $scope.errorMessage = $translate.instant('Username must be at least 3 characters long');
            return;
        }
        if ($scope.user.username.length > 50) {
            $scope.errorMessage = $translate.instant('Username must be at most 50 characters long');
            return;
        }
        if ($scope.user.password.length < 8) {
            $scope.errorMessage = $translate.instant('Password must be at least 8 characters long');
            return;
        }
        if ($scope.user.password.length > 50) {
            $scope.errorMessage = $translate.instant('Password must be at most 50 characters long');
            return;
        }

        if ($rootScope.randomToken) {
            Restangular.one('registration').post('create',
                JSON.stringify({
                    token: $rootScope.randomToken,
                    username: $scope.user.username,
                    password: $scope.user.password
                }),
                undefined,
                {'Content-Type': 'application/json;charset=utf-8'}
            ).then(function (resp) {
                if (resp.status === 1) {
                    $scope.registrationStatus = 1;
                    $rootScope.pendingPassword = $scope.user.password;
                    pollRegistrationStatus($rootScope.randomToken);
                }
            }, function (error) {
                if (error.data && error.data.type === 'AlreadyExistingUsername') {
                    $scope.errorMessage = $translate.instant('Username already exists.');
                } else {
                    $scope.errorMessage = $translate.instant('Registration failed. Please try again.');
                }
                $scope.registrationStatus = 0;
            });
        }
    };

    function pollRegistrationStatus(token) {
        Restangular.one('registration').post('create',
            JSON.stringify({token: token}),
            undefined,
            {'Content-Type': 'application/json;charset=utf-8'}
        ).then(function (resp) {
            var status = resp.status;
            $scope.registrationStatus = status;
            if (status === 2 && resp.username) {
                $scope.user = {
                    username: resp.username,
                    password: $rootScope.pendingPassword
                };
                $scope.login();
                delete $rootScope.pendingPassword;
            } else if (status === 3) {
                var title = $translate.instant('Registration Rejected');
                var msg = $translate.instant('Sorry, your registration was rejected by the admin!');
                var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
                $dialog.messageBox(title, msg, btns);
            } else if (status === 1) {
                setTimeout(function () {
                    pollRegistrationStatus(token);
                }, 2000);
            }
        }, function () {
            $scope.registrationStatus = 0;
            $scope.errorMessage = $translate.instant('Failed to check registration status.');
        });
    }

    // Login after a successful registration
    $scope.login = function () {
        User.login($scope.user).then(function () {
            User.userInfo(true).then(function (data) {
                $rootScope.userInfo = data;
            });

            if ($stateParams.redirectState !== undefined && $stateParams.redirectParams !== undefined) {
                $state.go($stateParams.redirectState, JSON.parse($stateParams.redirectParams))
                    .catch(function () {
                        $state.go('document.default');
                    });
            } else {
                $state.go('document.default');
            }
        }, function (data) {
            if (data.data.type === 'ValidationCodeRequired') {
                $scope.codeRequired = true;
            } else {
                var title = $translate.instant('login.login_failed_title');
                var msg = $translate.instant('login.login_failed_message');
                var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
                $dialog.messageBox(title, msg, btns);
            }
        });
    };
});