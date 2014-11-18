(function () {
    var cashFlow = angular.module('cashFlow', ['cashflow-operations', 'cashflow-account', 'cashflow-currency',
        'cashflow-category', 'ngRoute', 'ngTouch', 'ui.grid', 'ui.grid.selection', 'ui.bootstrap', 'ui.select', 'cgNotify']);

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

    // crud factories for models
    cashFlow.factory('operationFactory', ['$http', 'notify', 'errorFactory', function ($http, notify, errorFactory) {
        return {
            list: function (callback) {
                $http.get('action/operation/list').success(function (data) {
                    for (var i = 0; i < data.length; i++) {
                        data[i].date = new Date(data[i].date);
                    }
                    callback(data);
                }).error(function (data) {
                    errorFactory.handleError('Error while loading operations', data);
                });
            },
            get: function (id, callback) {
                $http.get('action/operation/get/' + id).success(function (data) {
                    callback(data);
                }).error(function (data) {
                    errorFactory.handleError('Error while loading operation', data);
                });
            },
            save: function (model, callback) {
                $http.post('action/operation/save', model).success(function (model) {
                    callback(model);
                    notify('Successfully saved');
                }).error(function (data) {
                    errorFactory.handleError('Error while saving operation', data);
                });
            },
            del: function (id, callback) {
                $http.get('action/operation/delete/' + id).success(function () {
                    notify('Operation deleted');
                    callback();
                }).error(function (data) {
                    errorFactory.handleError('Error while deleting operation', data);
                });
            }
        }
    }]);
    cashFlow.factory('accountFactory', ['$http', 'errorFactory', function ($http, errorFactory) {
        return {
            list: function (callback) {
                $http.get('action/account/list').success(function (data) {
                    callback(data);
                }).error(function (data) {
                    errorFactory.handleError('Error while loading accounts', data);
                });
            }
        }
    }]);
    cashFlow.factory('currencyFactory', ['$http', 'errorFactory', function ($http, errorFactory) {
        return {
            list: function (callback) {
                $http.get('action/currency/list').success(function (data) {
                    callback(data);
                }).error(function (data) {
                    errorFactory.handleError('Error while loading cuurencies', data);
                });
            }
        }
    }]);
    cashFlow.factory('categoryFactory', ['$http', 'errorFactory', function ($http, errorFactory) {
        return {
            list: function (callback) {
                $http.get('action/category/list').success(function (data) {
                    callback(data);
                }).error(function (data) {
                    errorFactory.handleError('Error while loading categories', data);
                });
            }
        }
    }]);

    // -------------------------------- LIBRARIES SETTINGS

    // select default UI theme for ui-select
    cashFlow.config(function (uiSelectConfig) {
        uiSelectConfig.theme = 'bootstrap';
    });

    // controller for navigation bar button
    cashFlow.controller('NavBarCtrl', ['$scope', function ($scope) {
        $scope.isCollapsed = true;
    }]);
})();