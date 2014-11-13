(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'ngRoute', 'ui.grid', 'ui.grid.selection', 'ui.grid.cellNav', 'ui.bootstrap', 'ui.select']);

    // fix for ui-bootstrap problem with angularjs 1.3
    cashFlow.directive('tooltip', function () {
        return {
            restrict: 'EA',
            link: function (scope, element, attrs) {
                attrs.tooltipTrigger = attrs.tooltipTrigger;
                attrs.tooltipPlacement = attrs.tooltipPlacement || 'top';
            }
        }
    });

    cashFlow.config(function (uiSelectConfig) {
        uiSelectConfig.theme = 'bootstrap';
    });

    cashFlow.config(function ($routeProvider) {
        $routeProvider

            .when('/', {
                templateUrl: 'operations.html',
                controller: 'OperationsCtrl'
            })

            .when('/category', {
                templateUrl: 'category.html',
                controller: 'CategoryCtrl'
            })

            .when('/currency', {
                templateUrl: 'currency.html',
                controller: 'CurrencyCtrl'
            })

            .when('/account', {
                templateUrl: 'account.html',
                controller: 'AccountCtrl'
            });
    });

    cashFlow.controller('NavBarCtrl', ['$scope', function ($scope) {
        $scope.isCollapsed = true;
    }]);
})();