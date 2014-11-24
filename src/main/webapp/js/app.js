(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'ngRoute', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.bootstrap', 'ui.select',
        'cgNotify', 'ngResource']);

    // -------------------------------- CONSTANTS
    cashFlow.constant('OPERATION_TYPE', {
        EXPENSE: 'EXPENSE',
        INCOME: 'INCOME',
        TRANSFER: 'TRANSFER'
    });

    // -------------------------------- LIBRARIES SETTINGS

    cashFlow.config(['$routeProvider', 'uiSelectConfig', function ($routeProvider, uiSelectConfig) {
        // route config
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

        //ui-selector theme
        uiSelectConfig.theme = 'bootstrap';
    }]);

    // -------------------------------- FACTORIES

    // crud factories for models
    cashFlow.factory('Operation', ['$resource', function ($resource) {
        return $resource('action/operation/:id', { id: '@id' }, {
            'update': { method:'PUT' }
        });
    }]);
    cashFlow.factory('Account', ['$resource', function ($resource) {
        return $resource('action/account/:id');
    }]);
    cashFlow.factory('Currency', ['$resource', function ($resource) {
        return $resource('action/currency/:id');
    }]);
    cashFlow.factory('Category', ['$resource', function ($resource) {
        return $resource('action/category/:id');
    }]);

    // factory for error handling
    cashFlow.factory('errorFactory', ['notify', function (notify) {
        return {
            handleError: function (title, data) {
                notify({
                    message: title + (data["message"] !== 'undefined' ? ': ' + data["message"] : ''),
                    classes: 'alert alert-danger'
                });

                if (data["stack"] !== 'undefined')
                    console.log(data["stack"]);
            }
        }
    }]);

    // common directives
    cashFlow.directive('numbersOnly', function () {
        return {
            require: 'ngModel',
            link: function (scope, element, attrs, modelCtrl) {
                modelCtrl.$parsers.push(function (inputValue) {
                    // this next if is necessary for when using ng-required on your input.
                    // In such cases, when a letter is typed first, this parser will be called
                    // again, and the 2nd time, the value will be undefined
                    if (inputValue == undefined) return '';
                    var transformedInput = inputValue.replace(/[^0-9+.]/g, '');
                    if (transformedInput != inputValue) {
                        modelCtrl.$setViewValue(transformedInput);
                        modelCtrl.$render();
                    }

                    return transformedInput;
                });
            }
        };
    });

    // controller for navigation bar button
    cashFlow.controller('NavBarCtrl', ['$scope', function ($scope) {
        $scope.isCollapsed = true;
    }]);
})();