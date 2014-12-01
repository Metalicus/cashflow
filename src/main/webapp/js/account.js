(function () {
    var account = angular.module('cashflow-account', []);

    account.controller('AccountCtrl', ['$scope', 'Account', '$modal', function ($scope, Account, $modal) {
        $scope.gridOptions = {
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            enableSorting: true,
            modifierKeysToMultiSelect: false,
            noUnselect: true,
            columnDefs: [
                {name: 'Name', field: 'name'},
                {name: 'Currency', field: 'currency.name'},
                {name: 'Current balance', field: 'balance'}
            ],
            serverData: {
                source: Account
            }
        };

        $scope.gridOptions.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
        };

        $scope.openNewDialog = function () {
            var modalInstance = $modal.open({
                templateUrl: 'template/account-modal.html',
                controller: 'AccountEditCtrl',
                resolve: {
                    id: function () {
                        return null;
                    }
                }
            });

            modalInstance.result.then(function (model) {
                $scope.gridOptions.data.unshift(model);
            });
        };

        $scope.openEditDialog = function () {
            var selectedRow = $scope.gridApi.selection.getSelectedRows()[0];
            var modalInstance = $modal.open({
                templateUrl: 'template/account-modal.html',
                controller: 'AccountEditCtrl',
                resolve: {
                    id: function () {
                        return selectedRow["id"];
                    }
                }
            });

            modalInstance.result.then(function (model) {
                var rowIndex = $scope.gridApi.grid.rowHashMap.get(selectedRow).i;
                $scope.gridOptions.data[rowIndex] = model;
            });
        };

        $scope.openDeleteDialog = function () {
            var selectedRow = $scope.gridApi.selection.getSelectedRows()[0];
            var modalInstance = $modal.open({
                templateUrl: 'template/delete-modal.html',
                controller: 'AccountDeleteCtrl',
                resolve: {
                    id: function () {
                        return selectedRow["id"];
                    }
                }
            });

            modalInstance.result.then(function () {
                var rowIndex = $scope.gridApi.grid.rowHashMap.get(selectedRow).i;
                $scope.gridOptions.data.splice(rowIndex, 1);
            });
        };
    }]);

    account.controller('AccountEditCtrl', ['$scope', '$modalInstance', 'Currency', 'Account', 'id', function ($scope, $modalInstance, Currency, Account, id) {
        $scope.model = new Account();
        $scope.model.id = id;

        $scope.submit = function () {
            if (id !== null) {
                $scope.model.$update(function (model) {
                    $modalInstance.close(model);
                });
            } else {
                $scope.model.$save(function (model) {
                    $modalInstance.close(model);
                });
            }
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $scope.currencies = Currency.query();

        if (id !== null)
            $scope.model.$get();
    }]);

    account.controller('AccountDeleteCtrl', ['$scope', '$modalInstance', 'Account', 'id', function ($scope, $modalInstance, Account, id) {
        $scope.entityName = 'currency';

        $scope.ok = function () {
            Account.delete({id: id}, function () {
                $modalInstance.close();
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
})();