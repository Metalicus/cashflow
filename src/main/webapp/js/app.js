(function () {
    var cashFlow = angular.module('cashFlow', ['ngRoute', 'ui.grid', 'ui.grid.selection', 'ui.grid.cellNav', 'ui.bootstrap']);

    cashFlow.config(function ($routeProvider) {
        $routeProvider

            .when('/', {
                templateUrl: 'operations.html',
                controller: 'mainController'
            })

            .when('/category', {
                templateUrl: 'category.html',
                controller: 'categoryController'
            })

            .when('/currency', {
                templateUrl: 'currency.html',
                controller: 'currencyController'
            })

            .when('/account', {
                templateUrl: 'account.html',
                controller: 'accountController'
            });
    });

    cashFlow.controller('mainController', ['$scope', '$http', '$modal', function ($scope, $http, $modal) {
        $scope.gridOptions = {
            enableRowHeaderSelection: false,
            multiSelect: false,
            enableSorting: true,
            columnDefs: [
                {name: 'Date', field: 'date', type: 'date', cellFilter: 'date:"yyyy-MM-dd"'},
                {name: 'Type', field: 'type'},
                {name: 'Account', field: 'account.name'},
                {name: 'Currency', field: 'currency.name'},
                {name: 'Amount', field: 'amount'},
                {name: 'Cross currency amount', field: 'crossCurrency.amount'},
                {name: 'Exchange rate', field: 'crossCurrency.exchangeRate'},
                {name: 'Info', filed: 'info'}
            ]
        };

        $scope.disabled = true;

        $scope.gridOptions.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;

            gridApi.cellNav.on.navigate($scope, function (newRowCol, oldRowCol) {
                $scope.gridApi.selection.selectRow(newRowCol.row.entity);
                $scope.disabled = $scope.gridApi.selection.getSelectedRows().length === 0;
            });
        };

        $scope.openEditDialog = function () {
            if ($scope.gridApi.selection.getSelectedRows().length > 0) {
                $modal.open({
                    templateUrl: 'template/operation-modal.html',
                    controller: 'OperationModelCtrl',
                    resolve: {
                        id: function () {
                            return $scope.gridApi.selection.getSelectedRows()[0]["id"];
                        }
                    }
                });
            }
        };

        $http.get('action/operation/list')
            .success(function (data) {
                for (var i = 0; i < data.length; i++) {
                    data[i].date = new Date(data[i].date);
                }
                $scope.gridOptions.data = data;
            });
    }]);

    cashFlow.controller('categoryController', ['$scope', '$http', function ($scope, $http) {
        $scope.gridOptions = {
            enableSorting: true,
            columnDefs: [
                {name: 'Name', field: 'name'}
            ]
        };

        $http.get('action/category/list')
            .success(function (data) {
                $scope.gridOptions.data = data;
            });
    }]);

    cashFlow.controller('currencyController', ['$scope', '$http', function ($scope, $http) {

    }]);

    cashFlow.controller('accountController', ['$scope', '$http', function ($scope, $http) {

    }]);

    cashFlow.controller('OperationModelCtrl', ['$scope', '$modalInstance', '$http', 'id', function ($scope, $modalInstance, $http, id) {
        $scope.id = id;
        $scope.date = {};
        $scope.amount = {};

        $scope.ok = function () {
            $modalInstance.dismiss('ok');
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $http.get('action/operation/get/' + id)
            .success(function (data) {
                $scope.date = new Date(data.date);
                $scope.amount = data.amount;
            });
    }]);
})();