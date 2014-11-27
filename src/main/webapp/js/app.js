(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'cashflow-reports', 'ngRoute', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.grid.infiniteScroll', 'ui.bootstrap',
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
            })

            .when('/reports', {
                templateUrl: 'reports.html',
                controller: 'ReportsCtrl'
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
            'query': {
                method: 'GET', isArray: true, transformResponse: function (data) {
                    return JSON.parse(data)['content'];
                }
            },
            'fetch': {method: 'GET'},
            'update': {method: 'PUT'}
        });
    }]);
    cashFlow.factory('Account', ['$resource', function ($resource) {
        return $resource('action/account/:id', {id: '@id'}, {
            'query': {
                method: 'GET', isArray: true, transformResponse: function (data) {
                    return JSON.parse(data)['content'];
                }
            },
            'update': {method: 'PUT'}
        });
    }]);
    cashFlow.factory('Currency', ['$resource', function ($resource) {
        return $resource('action/currency/:id', {id: '@id'}, {
            'query': {
                method: 'GET', isArray: true, transformResponse: function (data) {
                    return JSON.parse(data)['content'];
                }
            },
            'update': {method: 'PUT'}
        });
    }]);
    cashFlow.factory('Category', ['$resource', function ($resource) {
        return $resource('action/category/:id', {id: '@id'}, {
            'query': {
                method: 'GET', isArray: true, transformResponse: function (data) {
                    return JSON.parse(data)['content'];
                }
            },
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

                        options.springFetch = {
                            page: 0, // index of current page
                            last: false, // true is this is last page
                            getRequestParameters: function () {
                                var parameters = {page: options.springFetch.page, size: 100};
                                var sortedColumns = scope.gridApi.grid.getColumnSorting();

                                if (options.springFetch.springFilters) {
                                    for (var i = 0; i < options.springFetch.springFilters.length; i++)
                                        parameters[options.springFetch.springFilters[i]['name']] = options.springFetch.springFilters[i]['value'];
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
                            },
                            fetchData: function () {
                                var data = resource.fetch(options.springFetch.getRequestParameters(), function () {
                                    // if this is first page set last to false
                                    if (options.springFetch.page == 0)
                                        options.springFetch.last = false;

                                    // prevent fetching then last page
                                    if (options.springFetch.last)
                                        return;

                                    if (options.springFetch.page > 0) {
                                        // if this is not first page populate date to the exisiting array
                                        for (var i = 0; i < data['content'].length; i++)
                                            options.data.push(data['content'][i]);

                                    } else {
                                        // if this is first page just set new data
                                        options.data = data['content'];
                                    }

                                    options.springFetch.last = data['last'];
                                    ++options.springFetch.page;
                                    scope.gridApi.infiniteScroll.dataLoaded();
                                });
                            }
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
                        options.springFetch.fetchData();

                        // on sorting change
                        scope.gridApi.core.on.sortChanged(scope, function () {
                            options.springFetch.page = 0;
                            options.springFetch.fetchData();
                        });

                        // needs more data for infinite scroll
                        scope.gridApi.infiniteScroll.on.needLoadMoreData(scope, function () {
                            options.springFetch.fetchData();
                        });

                        // filtering
                        scope.filter = {};
                        scope.$watch('filter', function (newVal) {
                            if (newVal) {
                                var filters = [];
                                for (var name in newVal) {
                                    if (newVal.hasOwnProperty(name)) {
                                        if (newVal[name]) {
                                            filters.push({
                                                name: name,
                                                value: (newVal[name].hasOwnProperty('id') ? newVal[name]['id'] : newVal[name])
                                            });
                                        }
                                    }
                                }
                                options.springFetch.page = 0;
                                options.springFetch.springFilters = filters;
                                options.springFetch.fetchData();
                            }
                        }, true);
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