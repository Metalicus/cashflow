(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'cashflow-reports', 'cashflow-calculator', 'cashflow-configure', 'ngRoute', 'ngTouch',
        'ngCookies', 'ui.grid', 'ui.grid.selection', 'ui.grid.infiniteScroll', 'ui.bootstrap', 'ui.select',
        'toaster', 'ngResource', 'pascalprecht.translate']);

    // -------------------------------- CONSTANTS
    cashFlow.constant('OPERATION_TYPE', {
        EXPENSE: 'EXPENSE',
        INCOME: 'INCOME',
        TRANSFER: 'TRANSFER'
    });

    // -------------------------------- LIBRARIES SETTINGS

    cashFlow.config(['$routeProvider', 'uiSelectConfig', '$httpProvider', '$translateProvider',
        function ($routeProvider, uiSelectConfig, $httpProvider, $translateProvider) {
            // route config
            $routeProvider

                .when('/', {
                    redirectTo: '/operation'
                })

                .when('/operation', {
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
                    templateUrl: 'reports.html'
                })

                .when('/configure', {
                    templateUrl: 'configure.html'
                });

            //ui-selector theme
            uiSelectConfig.theme = 'bootstrap';

            // http provider
            $httpProvider.interceptors.push('errorHandlerInterceptor');

            // i18n
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });
            $translateProvider.preferredLanguage('en');
            $translateProvider.fallbackLanguage('en');
            $translateProvider.useLocalStorage();
        }]);

    // -------------------------------- FACTORIES

    cashFlow.factory('errorHandlerInterceptor', ['$q', 'toaster', '$filter', function ($q, toaster, $filter) {
        return {
            'responseError': function (response) {
                toaster.pop('error', $filter('translate')('error.title'), (response.data && response.data["message"] ? response.data["message"] : ''));

                if (response.data && response.data["stack"])
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

    // simple fetching without server sorting or filtering and without pageable support
    cashFlow.directive('serverData', function () {
        return {
            restrict: 'A',
            link: function (scope) {
                if (!scope.gridOptions["serverData"])
                    throw 'Required serverData.source is not found!';

                if (!scope.gridOptions["serverData"]["source"])
                    throw 'Required serverData.source is not found!';

                scope.gridOptions.data = scope.gridOptions["serverData"]["source"].query();
            }
        }
    });

    // support Spring data fetching in pages from server with sorting and filtering
    cashFlow.directive('pageable', function () {
        return {
            restrict: 'A',
            compile: function () {
                return {
                    post: function postLink(scope) {
                        if (!scope.gridOptions["serverData"])
                            throw 'Required serverData.source is not found!';

                        if (!scope.gridOptions["serverData"]["source"])
                            throw 'Required serverData.source is not found!';

                        var resource = scope.gridOptions["serverData"]["source"];
                        var options = scope.gridOptions;

                        options.serverData = {
                            page: 0, // index of current page
                            last: false, // true if this is last page
                            getRequestParameters: function () {
                                var parameters = {page: options.serverData.page, size: 100};
                                var sortedColumns = scope.gridApi.grid.getColumnSorting();

                                if (options.serverData.filters) {
                                    for (var i = 0; i < options.serverData.filters.length; i++)
                                        parameters[options.serverData.filters[i]['name']] = options.serverData.filters[i]['value'];
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
                                // if this is first page set last to false
                                if (options.serverData.page == 0)
                                    options.serverData.last = false;

                                // prevent fetching then last page
                                if (options.serverData.last)
                                    return;

                                var data = resource.fetch(options.serverData.getRequestParameters(), function () {
                                    if (options.serverData.page > 0) {
                                        // if this is not first page populate date to the exisiting array
                                        for (var i = 0; i < data['content'].length; i++)
                                            options.data.push(data['content'][i]);

                                    } else {
                                        // if this is first page just set new data
                                        options.data = data['content'];
                                    }

                                    options.serverData.last = data['last'];
                                    ++options.serverData.page;
                                    scope.gridApi.infiniteScroll.dataLoaded();
                                });
                            },
                            resort: function () {
                                options.serverData.page = 0;
                                options.serverData.fetchData();
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
                            options.defaultSort = {
                                name: 'id',
                                dir: 'desc'
                            };
                        }

                        // first loading
                        options.serverData.fetchData();

                        // on sorting change
                        scope.gridApi.core.on.sortChanged(scope, function () {
                            options.serverData.resort();
                        });

                        // needs more data for infinite scroll
                        scope.gridApi.infiniteScroll.on.needLoadMoreData(scope, function () {
                            options.serverData.fetchData();
                        });

                        // filtering
                        scope.filter = {};
                        scope.$watch('filter', function (newVal, oldVal) {
                            if (newVal && newVal !== oldVal) {
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
                                options.serverData.page = 0;
                                options.serverData.filters = filters;
                                options.serverData.fetchData();
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