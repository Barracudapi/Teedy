'use strict';

/**
 * Document activity controller.
 */
angular.module('docs').controller('DocumentActivity', function ($scope, $stateParams, Restangular, $translate) {
    $scope.activity = {
        progress: 0
    };

    $scope.loadActivity = function () {
        Restangular.one('user-activity/user')
            .get({
                entity_id: $stateParams.id,
                limit: 1
            })
            .then(function (responseData) {
                if (responseData.activities && responseData.activities.length > 0) {
                    $scope.activity.id = responseData.activities[0].id;
                    $scope.activity.progress = responseData.activities[0].progress;

                    if (responseData.activities[0].planned_date_timestamp) {
                        try {
                            const plannedTimestamp = responseData.activities[0].planned_date_timestamp;
                            let plannedDateObj;

                            if (typeof plannedTimestamp === 'string') {
                                plannedDateObj = new Date(parseInt(plannedTimestamp, 10));
                            } else {
                                plannedDateObj = new Date(plannedTimestamp);
                            }

                            if (!isNaN(plannedDateObj.getTime())) {
                                $scope.activity.planned_date = plannedDateObj;
                            }
                        } catch (e) {
                            console.error("Error parsing planned date:", e);
                        }
                    }
                }
            });
    };

    $scope.loadActivity();

    $scope.saveActivity = function () {
        const activityToSave = angular.copy($scope.activity);
        activityToSave.entity_id = $stateParams.id;

        if (activityToSave.planned_date) {
            const plannedDateInstance = new Date(activityToSave.planned_date);
            if (!isNaN(plannedDateInstance.getTime())) {
                activityToSave.planned_date_timestamp = plannedDateInstance.getTime();
            }
        }

        Restangular.one('user-activity/create-or-update').put(activityToSave).then(function (responseData) {
            $scope.activity.id = responseData.id;
            $scope.activitySaved = true;
            setTimeout(function () {
                $scope.$apply(function () {
                    $scope.activitySaved = false;
                });
            }, 2000);
        });
    };

    $scope.formatProgress = function (progressValue) {
        if (progressValue === 100) {
            return $translate.instant('settings.user_activities.status.completed');
        } else if (progressValue > 0) {
            return $translate.instant('settings.user_activities.status.in_progress') + ' (' + progressValue + '%)';
        } else {
            return $translate.instant('settings.user_activities.status.not_started');
        }
    };
});