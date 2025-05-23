"use strict";

/**
 * Document activity controller.
 */
angular
  .module("docs")
  .controller(
    "DocumentActivity",
    function ($scope, $stateParams, Restangular, $translate) {
      // Initialize activity model
      $scope.activity = {
        progress: 0,
      };

      // Load existing activity for this document
      $scope.loadActivity = function () {
        Restangular.one("useractivity/user")
          .get({
            entity_id: $stateParams.id,
            limit: 1,
          })
          .then(function (data) {
            if (data.activities && data.activities.length > 0) {
              // If an activity exists for this document, load it
              $scope.activity.id = data.activities[0].id;
              $scope.activity.progress = data.activities[0].progress;

              // Format the planned date for the datepicker
              if (data.activities[0].planned_date_timestamp) {
                try {
                  var timestamp = data.activities[0].planned_date_timestamp;
                  var planned;

                  // Handle string or number timestamp
                  if (typeof timestamp === "string") {
                    planned = new Date(parseInt(timestamp, 10));
                  } else {
                    planned = new Date(timestamp);
                  }

                  // Check if valid date
                  if (!isNaN(planned.getTime())) {
                    $scope.activity.planned_date = planned
                      .toISOString()
                      .substring(0, 10);
                  }
                } catch (e) {
                  console.error("Error parsing planned date:", e);
                }
              }
            }
          });
      };

      // Save the activity
      $scope.saveActivity = function () {
        // Make a copy of the activity and add the document ID
        var activity = angular.copy($scope.activity);
        activity.entity_id = $stateParams.id;

        // Convert planned_date from datepicker format to timestamp
        if (activity.planned_date) {
          var deadline = new Date(activity.planned_date);
          if (!isNaN(deadline.getTime())) {
            activity.planned_date_timestamp = deadline.getTime();
          }
        }

        // Save or update the activity
        Restangular.one("useractivity")
          .put(activity)
          .then(function (data) {
            // Update the ID in case it was a new activity
            $scope.activity.id = data.id;
            // Show success message
            $scope.activitySaved = true;
            setTimeout(function () {
              $scope.$apply(function () {
                $scope.activitySaved = false;
              });
            }, 2000);
          });
      };

      // Format the progress for display
      $scope.formatProgress = function (progress) {
        if (progress === 100) {
          return $translate.instant(
            "settings.user_activities.status.completed"
          );
        } else if (progress > 0) {
          return (
            $translate.instant("settings.user_activities.status.in_progress") +
            " (" +
            progress +
            "%)"
          );
        } else {
          return $translate.instant(
            "settings.user_activities.status.not_started"
          );
        }
      };

      // Get the progress bar class
      $scope.getProgressClass = function (progress) {
        if (progress === 100) {
          return "progress-bar-success";
        } else if (progress > 0) {
          return "progress-bar-warning";
        } else {
          return "progress-bar-danger";
        }
      };

      // Initialize by loading existing activity
      $scope.loadActivity();
    }
  );
