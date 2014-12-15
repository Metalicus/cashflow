(function () {
    var reports = angular.module('cashflow-reports', []);

    reports.constant('REPORT_TYPE', {
        MONTHLY_BALANCE: 'MONTHLY_BALANCE'
    });

    //controller for list-group
    reports.controller('ReportListCtrl', ['$scope', 'REPORT_TYPE', function ($scope, REPORT_TYPE) {
        $scope.REPORT_TYPE = REPORT_TYPE;
        $scope.reports = Object.keys(REPORT_TYPE);
        $scope.current = $scope.reports[0];

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

    reports.controller('MonthlyBalanceCrtl', ['$scope', 'REPORT_TYPE', '$http', '$filter', function ($scope, REPORT_TYPE, $http, $filter) {
        var currentDate = new Date();
        $scope.months = [
            {index: 0, name: $filter('translate')('reports.months.jan')},
            {index: 1, name: $filter('translate')('reports.months.feb')},
            {index: 2, name: $filter('translate')('reports.months.mar')},
            {index: 3, name: $filter('translate')('reports.months.apr')},
            {index: 4, name: $filter('translate')('reports.months.may')},
            {index: 5, name: $filter('translate')('reports.months.jun')},
            {index: 6, name: $filter('translate')('reports.months.jul')},
            {index: 7, name: $filter('translate')('reports.months.aug')},
            {index: 8, name: $filter('translate')('reports.months.sep')},
            {index: 9, name: $filter('translate')('reports.months.oct')},
            {index: 10, name: $filter('translate')('reports.months.nov')},
            {index: 11, name: $filter('translate')('reports.months.dec')}
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