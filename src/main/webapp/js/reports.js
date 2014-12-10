(function () {
    var reports = angular.module('cashflow-reports', []);

    reports.constant('REPORT_TYPE', {
        MONTHLY_BALANCE: 'MONTHLY_BALANCE'
    });

    //controller for list-group
    reports.controller('ReportListCtrl', ['$scope', 'REPORT_TYPE', function ($scope, REPORT_TYPE) {
        $scope.REPORT_TYPE = REPORT_TYPE;
        $scope.current = Object.keys(REPORT_TYPE)[0];

        $scope.isActive = function (report) {
            return $scope.current === report;
        };

        $scope.selectReport = function (current) {
            $scope.current = current;
        };

        // method for preventing click on list item
        $scope.preventClick = function (event) {
            event.preventDefault();
            event.stopPropagation();
        };
    }]);

    reports.controller('MonthlyBalanceCrtl', ['$scope', 'REPORT_TYPE', '$http', function ($scope, REPORT_TYPE, $http) {
        var currentDate = new Date();
        $scope.months = [
            {index: 0, name: 'January'},
            {index: 1, name: 'February'},
            {index: 2, name: 'March'},
            {index: 3, name: 'April'},
            {index: 4, name: 'May'},
            {index: 5, name: 'June'},
            {index: 6, name: 'July'},
            {index: 7, name: 'August'},
            {index: 8, name: 'September'},
            {index: 9, name: 'October'},
            {index: 10, name: 'November'},
            {index: 11, name: 'December'}
        ];
        $scope.years = [2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020];

        $scope.request = {
            type: REPORT_TYPE.MONTHLY_BALANCE,
            month: $scope.months[currentDate.getMonth()],
            year: currentDate.getFullYear()
        };

        $scope.report = {};

        $scope.loadReport = function () {
            $http.get('action/report/' + $scope.request.type + '?month=' + $scope.request.month.index + '&year=' + $scope.request.year).then(function (response) {
                $scope.report = response.data;
            })
        };

        $scope.$watch('request', function (newVal, oldVal) {
            if (newVal && newVal !== oldVal) {
                $scope.loadReport();
            }
        }, true);

        $scope.loadReport();
    }]);
})();