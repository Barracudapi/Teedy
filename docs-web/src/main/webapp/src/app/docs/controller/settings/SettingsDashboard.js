'use strict';

/**
 * AI-generated-content
 * tool: Grok
 * version: 3
 * usage: I asked the AI model to generate a controller for a settings dashboard in an AngularJS application. I copied the code and learned from it.
 */

/**
 * Controller for the settings activity dashboard.
 */
angular.module('docs').controller('SettingsDashboard', function ($scope, $state, Restangular, $translate) {
    $scope.isLoading = false;
    $scope.userActivities = [];
    $scope.totalActivities = 0;
    $scope.isGanttVisible = true;
    $scope.selectedUser = null;
    $scope.selectedType = null;
    $scope.userFilterOptions = [];
    $scope.typeFilterOptions = [];

    $scope.ganttChart = {
        data: [],
        timeScale: {
            from: new Date(),
            to: new Date(),
        }
    };

    // Fetch activities from server with optional filters
    $scope.fetchActivities = function () {
        $scope.isLoading = true;

        const param = {};

        if ($scope.selectedUser) {
            param.user_id = $scope.selectedUser;
        }

        Restangular.one('user-activity/all')
            .get(param)
            .then(function (data) {
                $scope.userActivities = data.activities;
                $scope.totalActivities = data.total;
                $scope.isLoading = false;

                extractFilters();

                if ($scope.isGanttVisible) {
                    prepareGanttData();
                }
            });
    };

    // Delete a specific activity from the list
    $scope.removeActivity = function (activity) {
        if (confirm($translate.instant('settings.dashboard.delete_confirmation'))) {
            Restangular.one('user-activity/delete', activity.id).remove().then(function () {
                $scope.userActivities = $scope.userActivities.filter(function (a) {
                    return a.id !== activity.id;
                });
                $scope.totalActivities--;

                if ($scope.isGanttVisible) {
                    prepareGanttData();
                }
            });
        }
    };

    // Collect distinct users from activities for filtering
    function extractFilters() {
        const users = {};

        $scope.userActivities.forEach(function (activity) {
            users[activity.user_id] = activity.username;
        });

        $scope.userFilterOptions = Object.keys(users).map(function (id) {
            return {id: id, name: users[id]};
        });
    }

    // Create Gantt-compatible data from activities
    function prepareGanttData() {
        const ganttRows = [];
        let minDate = new Date();
        let maxDate = new Date();

        minDate.setDate(minDate.getDate());
        maxDate.setDate(maxDate.getDate() + 60);

        const userGroups = {};

        $scope.userActivities.forEach(function (activity) {
            if (!userGroups[activity.username]) {
                userGroups[activity.username] = [];
            }

            const startDate = activity.create_timestamp ? new Date(activity.create_timestamp) : new Date();
            let endDate;

            if (activity.completed_date_timestamp) {
                endDate = new Date(activity.completed_date_timestamp);
            } else if (activity.planned_date_timestamp) {
                endDate = new Date(activity.planned_date_timestamp);
            } else {
                endDate = new Date(startDate);
                endDate.setDate(endDate.getDate() + 7);
            }

            if (startDate < minDate) minDate = startDate;
            if (endDate > maxDate) maxDate = endDate;

            userGroups[activity.username].push({
                id: activity.id,
                name: activity.entity_name,
                start: startDate,
                end: endDate,
                progress: activity.progress,
                color: getTaskColor(activity.progress)
            });
        });

        Object.keys(userGroups).forEach(function (username) {
            const tasks = userGroups[username].sort(function (a, b) {
                return a.start - b.start;
            });

            const processedTasks = [];
            tasks.forEach(function (task) {
                task.verticalPosition = 0;

                const overlappingTasks = processedTasks.filter(function (existingTask) {
                    return !(task.end <= existingTask.start || task.start >= existingTask.end);
                });

                if (overlappingTasks.length > 0) {
                    const usedPositions = overlappingTasks.map(function (t) {
                        return t.verticalPosition;
                    });

                    let position = 0;
                    while (usedPositions.indexOf(position) !== -1) {
                        position++;
                    }

                    task.verticalPosition = position;
                }

                processedTasks.push(task);
            });

            ganttRows.push({
                name: username,
                tasks: processedTasks
            });
        });

        $scope.ganttChart = {
            data: ganttRows,
            timeScale: {
                from: minDate,
                to: maxDate
            }
        };
    }

    // Build date steps to be shown on Gantt chart
    $scope.generateTimelineDates = function (startDate, endDate) {
        const dates = [];
        const currentDate = new Date(startDate);
        const end = new Date(endDate);
        const step = 3;

        while (currentDate <= end) {
            dates.push(new Date(currentDate));
            currentDate.setDate(currentDate.getDate() + step);
        }

        return dates;
    };

    // Compute CSS styles for each Gantt task
    $scope.calculateTaskStyle = function (task, timeScale) {
        const startDate = new Date(task.start);
        const endDate = new Date(task.end);
        const timeScaleStart = new Date(timeScale.from);
        const timeScaleEnd = new Date(timeScale.to);

        const totalTimeMs = timeScaleEnd - timeScaleStart;
        const startOffset = Math.max(0, startDate - timeScaleStart);
        let duration = Math.min(endDate - startDate, endDate - timeScaleStart);

        duration = Math.max(duration, 36 * 60 * 60 * 1000);

        let left = (startOffset / totalTimeMs) * 100;
        let width = (duration / totalTimeMs) * 100;

        if (left > 100) left = 100;
        if (left + width > 100) width = 100 - left;

        if (left < 0.5) left = 0.5;
        if (left + width > 99) width = 99 - left;

        const topPosition = 10 + (task.verticalPosition || 0) * 35;

        const backgroundColor = lightenColor(task.color, 30);
        const borderColor = darkenColor(task.color, 10);

        return {
            left: left + '%',
            width: width + '%',
            top: topPosition + 'px',
            backgroundColor: backgroundColor,
            borderColor: borderColor
        };
    };

    // Lighten a given hex color
    function lightenColor(color, percent) {
        if (!color) return '#f8f8f8';

        if (color.startsWith('#')) {
            const num = parseInt(color.slice(1), 16);
            let r = (num >> 16) + percent;
            let g = ((num >> 8) & 0x00FF) + percent;
            let b = (num & 0x0000FF) + percent;

            r = Math.min(r, 255);
            g = Math.min(g, 255);
            b = Math.min(b, 255);

            return '#' + (
                (r << 16) +
                (g << 8) +
                b
            ).toString(16).padStart(6, '0');
        }

        return color;
    }

    // Darken a given hex color
    function darkenColor(color, percent) {
        if (!color) return '#ddd';

        if (color.startsWith('#')) {
            const num = parseInt(color.slice(1), 16);
            let r = (num >> 16) - percent;
            let g = ((num >> 8) & 0x00FF) - percent;
            let b = (num & 0x0000FF) - percent;

            r = Math.max(r, 0);
            g = Math.max(g, 0);
            b = Math.max(b, 0);

            return '#' + (
                (r << 16) +
                (g << 8) +
                b
            ).toString(16).padStart(6, '0');
        }

        return color;
    }

    // Assign color based on progress percentage
    function getTaskColor(progress) {
        if (progress === 100) {
            return '#5cb85c';
        } else if (progress >= 70) {
            return '#5bc0de';
        } else if (progress >= 30) {
            return '#f0ad4e';
        } else {
            return '#d9534f';
        }
    }

    // Reapply filters to reload activity list
    $scope.refreshActivities = function () {
        $scope.fetchActivities();
    };

    // Convert timestamp to readable date
    $scope.formatTimestamp = function (timestamp) {
        if (!timestamp) return '';

        var date;
        try {
            date = new Date(timestamp);

            if (isNaN(date.getTime())) {
                date = new Date(parseInt(timestamp, 10));
            }

            if (isNaN(date.getTime())) {
                console.warn("Invalid date timestamp:", timestamp);
                return '';
            }

            return date.toLocaleDateString();
        } catch (e) {
            console.error("Error formatting date:", e);
            return '';
        }
    };

    // Return a translated progress label
    $scope.getProgressLabel = function (progress) {
        if (progress === 100) {
            return $translate.instant('settings.dashboard.status.completed');
        } else if (progress > 0) {
            return $translate.instant('settings.dashboard.status.in_progress') + ' (' + progress + '%)';
        } else {
            return $translate.instant('settings.dashboard.status.not_started');
        }
    };

    $scope.fetchActivities();
});