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

    // support Spring data fetching from server with sorting
    cashFlow.directive('springFetch', function () {
        return {
            restrict: 'A',
            compile: function () {
                return {
                    post: function postLink(scope, iElement, iAttrs) {

                        if (!iAttrs['springFetch'])
                            throw "$resource factory is required!";

                        var resource = iElement.injector().get(iAttrs['springFetch']);
                        var options = scope.gridOptions;

                        options.page = 0;
                        options.getRequestParameters = function (filters) {
                            var parameters = {page: options.page, size: 100};
                            var sortedColumns = scope.gridApi.grid.getColumnSorting();

                            if (options.springFilters) {
                                for (var i = 0; i < options.springFilters.length; i++)
                                    parameters[options.springFilters[i]['name']] = options.springFilters[i]['value'];
                            }

                            if (sortedColumns.length == 0) {
                                // default sort
                                parameters['sort'] = options.defaultSort.name + ',' + options.defaultSort.dir;
                            } else {
                                for (i = 0; i < sortedColumns.length; i++) {
                                    parameters['sort'] = scope.gridApi.grid.getColumnSorting()[0].field + ',' + scope.gridApi.grid.getColumnSorting()[0].sort.direction;
                                }
                            }

                            return parameters;
                        };
                        options.fetchData = function (partial) {
                            var data = options.data = resource.query(options.getRequestParameters(), function () {

                                if (partial) {
                                    for (var i = 0; i < data.length; i++)
                                        options.data.push(data[i]);
                                } else {
                                    options.data = data;
                                }

                                ++options.page;
                                scope.gridApi.infiniteScroll.dataLoaded();
                            });
                        };

                        // find default sort column
                        for (var i = 0; i < options.columnDefs.length; i++) {
                            if (options.columnDefs[i].defaultSort) {

                                options.defaultSort = {
                                    name: options.columnDefs[i].field,
                                    dir: options.columnDefs[i].defaultSort.direction
                                };

                                break;
                            }
                        }

                        // if no default sort when create sort by id
                        if (!options.defaultSort) {
                            options.defaultSort.name = 'id';
                            options.defaultSort.dir = 'desc';
                        }

                        // first loading
                        options.fetchData();

                        // on sorting change
                        scope.gridApi.core.on.sortChanged(scope, function () {
                            options.page = 0;
                            options.fetchData();
                        });

                        // needs more data for infinite scroll
                        scope.gridApi.infiniteScroll.on.needLoadMoreData(scope, function () {
                            options.fetchData();
                        });
                    }
                }
            }
        }
    });

    // add filtering support, depends on spring-infinite
    cashFlow.directive('springFilter', function () {
        return {
            restrict: 'A',
            link: function (scope) {
                scope.filter = {};
                scope.$watch('filter', function (newVal) {
                    if (newVal) {
                        var filters = [];
                        for (var name in newVal) {
                            if (newVal.hasOwnProperty(name)) {
                                filters.push({
                                    name: name,
                                    value: (newVal[name].hasOwnProperty('id') ? newVal[name]['id'] : newVal[name])
                                });
                            }
                        }
                        scope.gridOptions.page = 0;
                        scope.gridOptions.springFilters = filters;
                        scope.gridOptions.fetchData();
                    }
                }, true);
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