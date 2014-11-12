(function () {
    var operations = angular.module('cashflow-operations', []);

    operations.directive('dpicker', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/dpicker.html'
        }
    });

    operations.directive('selectAccount', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/select-account.html'
        }
    });

    operations.directive('selectCurrency', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/select-currency.html'
        }
    });

    operations.directive('selectCategory', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/select-category.html'
        }
    });

    operations.controller('OperationsCtrl', ['$scope', '$http', '$modal', function ($scope, $http, $modal) {
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

            gridApi.cellNav.on.navigate($scope, function (newRowCol) {
                $scope.gridApi.selection.selectRow(newRowCol.row.entity);
                $scope.disabled = $scope.gridApi.selection.getSelectedRows().length === 0;
            });
        };

        $scope.openNewDialog = function () {
            $modal.open({
                templateUrl: 'template/operation-modal.html',
                controller: 'OperationEditCtrl',
                resolve: {
                    id: function () {
                        return null;
                    }
                }
            });
        };

        $scope.openEditDialog = function () {
            if ($scope.gridApi.selection.getSelectedRows().length > 0) {
                $modal.open({
                    templateUrl: 'template/operation-modal.html',
                    controller: 'OperationEditCtrl',
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

    operations.controller('OperationEditCtrl', ['$scope', '$modalInstance', '$http', 'id', function ($scope, $modalInstance, $http, id) {
        // operation model
        $scope.model = {
            id: id,
            type: 'OUTCOME',
            date: new Date(),
            amount: null,
            moneyWas: null,
            moneyBecome: null,
            account: {},
            currency: {},
            category: {}
        };

        // ui-select models
        $scope.account = {};
        $scope.currency = {};
        $scope.category = {};

        $scope.tabs = [
            {type: 'OUTCOME', disabled: false, active: true},
            {type: 'TRANSFER', disabled: false, active: false},
            {type: 'INCOME', disabled: false, active: false}
        ];

        $scope.updateMoney = function () {

        };

        $scope.accountSelect = function (model) {
            $scope.model.account = model;
            $scope.model.moneyWas = model["balance"];
        };

        $scope.categorySelect = function (model) {
            $scope.model.category = model;
        };

        $scope.currencySelect = function (model) {
            $scope.model.currency = model;
        };

        $scope.setType = function (index) {
            if ($scope.model.id === null) {
                $scope.model.type = $scope.tabs[index].type;

                for (var i = 0; i < $scope.tabs.length; i++)
                    $scope.tabs[i].active = false;

                $scope.tabs[index].active = true;
            }
        };

        $scope.isActived = function (index) {
            return $scope.tabs[index].active;
        };

        $scope.isTabDisabled = function (index) {
            return $scope.tabs[index].disabled;
        };

        $scope.submit = function () {
            $modalInstance.dismiss('ok');
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $http.get('action/account/list')
            .then(function (response) {
                $scope.accounts = response.data;
            });

        $http.get('action/currency/list')
            .then(function (response) {
                $scope.currencies = response.data;
            });

        $http.get('action/category/list')
            .then(function (response) {
                $scope.categories = response.data;
            });

        if (id !== null) {
            $http.get('action/operation/get/' + id)
                .success(function (data) {
                    //model
                    $scope.model.type = data.type;
                    $scope.model.date = new Date(data.date);
                    $scope.model.amount = data.amount;
                    $scope.model.moneyWas = data.moneyWas;
                    $scope.model.moneyBecome = data.moneyBecome;
                    $scope.model.account = data.account;
                    $scope.model.currency = data.currency;
                    $scope.model.category = data.category;

                    //ui-select
                    $scope.account.selected = data.account;
                    $scope.currency.selected = data.currency;
                    $scope.category.selected = data.category;

                    //tabs settings
                    for (var i = 0; i < $scope.tabs.length; i++) {
                        if ($scope.tabs[i].type === data.type) {
                            $scope.tabs[i].disabled = false;
                            $scope.tabs[i].active = true;
                        } else {
                            $scope.tabs[i].disabled = true;
                            $scope.tabs[i].active = false;
                        }
                    }
                });
        }
    }]);
})();