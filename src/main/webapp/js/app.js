(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'ngRoute', 'ui.grid', 'ui.grid.selection', 'ui.grid.cellNav', 'ui.bootstrap', 'ui.select']);

    // route config
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

    // crud factories for models
    cashFlow.factory('operationFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/operation/list').then(function (response) {
                    callback(response.data);
                });
            },
            get: function (id, callback) {
                $http.get('action/operation/get/' + id).then(function (response) {
                    callback(response.data);
                });
            }
        }
    }]);
    cashFlow.factory('accountFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/account/list').then(function (response) {
                    callback(response.data);
                });
            }
        }
    }]);
    cashFlow.factory('currencyFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/currency/list').then(function (response) {
                    callback(response.data);
                });
            }
        }
    }]);
    cashFlow.factory('categoryFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/category/list').then(function (response) {
                    callback(response.data);
                });
            }
        }
    }]);

    // -------------------------------- LIBRARIES SETTINGS

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

    // select default UI theme for ui-select
    cashFlow.config(function (uiSelectConfig) {
        uiSelectConfig.theme = 'bootstrap';
    });

    // controller for navigation bar button
    cashFlow.controller('NavBarCtrl', ['$scope', function ($scope) {
        $scope.isCollapsed = true;
    }]);
})();