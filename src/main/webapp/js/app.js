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

    // crud factories for models
    cashFlow.factory('operationFactory', ['$http', 'notify', function ($http, notify) {
        return {
            list: function (callback) {
                $http.get('action/operation/list').success(function (data) {
                    for (var i = 0; i < data.length; i++) {
                        data[i].date = new Date(data[i].date);
                    }
                    callback(data);
                }).error(function () {
                    notify({
                        message: 'Error while loading operations',
                        classes: 'alert alert-danger'
                    });
                });
            },
            get: function (id, callback) {
                $http.get('action/operation/get/' + id).success(function (data) {
                    callback(data);
                }).error(function () {
                    notify({
                        message: 'Error while loading operation',
                        classes: 'alert alert-danger'
                    });
                });
            },
            save: function (model, callback) {
                $http.post('action/operation/save', model).success(function (model) {
                    callback(model);
                    notify('Successfully saved');
                }).error(function () {
                    notify({
                        message: 'Error while saving operation',
                        classes: 'alert alert-danger'
                    });
                });
            }
        }
    }]);
    cashFlow.factory('accountFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/account/list').success(function (data) {
                    callback(data);
                }).error(function () {
                    notify({
                        message: 'Error while loading accounts',
                        classes: 'alert alert-danger'
                    });
                });
            }
        }
    }]);
    cashFlow.factory('currencyFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/currency/list').success(function (data) {
                    callback(data);
                }).error(function () {
                    notify({
                        message: 'Error while loading cuurencies',
                        classes: 'alert alert-danger'
                    });
                });
            }
        }
    }]);
    cashFlow.factory('categoryFactory', ['$http', function ($http) {
        return {
            list: function (callback) {
                $http.get('action/category/list').success(function (data) {
                    callback(data);
                }).error(function () {
                    notify({
                        message: 'Error while loading categories',
                        classes: 'alert alert-danger'
                    });
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