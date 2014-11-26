(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'ngRoute', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.grid.infiniteScroll', 'ui.bootstrap',
        'ui.select', 'toaster', 'ngResource']);

    // -------------------------------- CONSTANTS
    cashFlow.constant('OPERATION_TYPE', {
        EXPENSE: 'EXPENSE',
        INCOME: 'INCOME',
        TRANSFER: 'TRANSFER'
    });

    // -------------------------------- LIBRARIES SETTINGS

    cashFlow.config(['$routeProvider', 'uiSelectConfig', '$httpProvider', function ($routeProvider, uiSelectConfig, $httpProvider) {
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

        // http provider
        $httpProvider.interceptors.push('errorHandlerInterceptor');
    }]);

    // -------------------------------- FACTORIES

    cashFlow.factory('errorHandlerInterceptor', ['$q', 'toaster', function ($q, toaster) {
        return {
            'responseError': function (response) {
                toaster.pop('error', "Error", (response.data["message"] !== 'undefined' ? ': ' + response.data["message"] : ''));

                if (response.data["stack"] !== 'undefined')
                    console.log(response.data["stack"]);

                return $q.reject(response);
            }
        }
    }]);

    // crud factories for models
    cashFlow.factory('Operation', ['$resource', function ($resource) {
        return $resource('action/operation/:id', {id: '@id'}, {
            'update': {method: 'PUT'}
        });
    }]);
    cashFlow.factory('Account', ['$resource', function ($resource) {
        return $resource('action/account/:id', {id: '@id'}, {
            'update': {method: 'PUT'}
        });
    }]);
    cashFlow.factory('Currency', ['$resource', function ($resource) {
        return $resource('action/currency/:id', {id: '@id'}, {
            'update': {method: 'PUT'}
        });
    }]);
    cashFlow.factory('Category', ['$resource', function ($resource) {
        return $resource('action/category/:id', {id: '@id'}, {
            'update': {method: 'PUT'}
        });
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

    // support Spring data fetching from server with sorting. 'factory-name' attribute is required
    cashFlow.directive('springInfinite', function () {
        return {
            restrict: 'A',
            compile: function () {
                return {
                    post: function postLink(scope, iElement, iAttrs) {

                        if (!iAttrs['factoryName'])
                            throw "'factory-name' attribute is required!";

                        var resource = iElement.injector().get(iAttrs['factoryName']);

                        scope.gridOptions.page = 1;
                        scope.gridOptions.getRequestParameters = function () {
                            var parameters = {size: scope.gridOptions.page * 100};
                            var sortedColumns = scope.gridApi.grid.getColumnSorting();

                            // default sort
                            if (sortedColumns.length == 0) {
                                parameters['sort'] = scope.gridOptions.defaultSort.name + ',' + scope.gridOptions.defaultSort.dir;
                            } else {
                                for (var i = 0; i < sortedColumns.length; i++) {
                                    parameters['sort'] = scope.gridApi.grid.getColumnSorting()[0].field + ',' + scope.gridApi.grid.getColumnSorting()[0].sort.direction;
                                }
                            }

                            return parameters;
                        };

                        // find default sort column
                        for (var i = 0; i < scope.gridOptions.columnDefs.length; i++) {
                            if (scope.gridOptions.columnDefs[i].defaultSort) {

                                scope.gridOptions.defaultSort = {
                                    name: scope.gridOptions.columnDefs[i].field,
                                    dir: scope.gridOptions.columnDefs[i].defaultSort.direction
                                };

                                break;
                            }
                        }

                        // if no default sort when create sort by id
                        if (!scope.gridOptions.defaultSort) {
                            scope.gridOptions.defaultSort.name = 'id';
                            scope.gridOptions.defaultSort.dir = 'desc';
                        }

                        // first loading
                        scope.gridOptions.data = resource.query(scope.gridOptions.getRequestParameters(), function () {
                            ++scope.gridOptions.page;
                        });

                        // on sorting change
                        scope.gridApi.core.on.sortChanged(scope, function () {
                            scope.gridOptions.page = 1;
                            var data = scope.gridOptions.data = resource.query(scope.gridOptions.getRequestParameters(), function () {
                                scope.gridOptions.data = data;
                                ++scope.gridOptions.page;
                                scope.gridApi.infiniteScroll.dataLoaded();
                            });
                        });

                        // needs more data for infinite scroll
                        scope.gridApi.infiniteScroll.on.needLoadMoreData(scope, function () {
                            var data = resource.query(scope.gridOptions.getRequestParameters(), function () {
                                scope.gridOptions.data = data;
                                ++scope.gridOptions.page;
                                scope.gridApi.infiniteScroll.dataLoaded();
                            });
                        });
                    }
                }
            }
        }
    });

    // controller for navigation bar button
    cashFlow.controller('NavBarCtrl', ['$scope', '$location', function ($scope, $location) {
        $scope.isCollapsed = true;

        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };
    }]);
})();