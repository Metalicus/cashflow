(function () {
    var currency = angular.module('cashflow-currency', []);

    currency.controller('CurrencyCtrl', ['$scope', 'Currency', '$modal', function ($scope, Currency, $modal) {
        $scope.gridOptions = {
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            enableSorting: true,
            modifierKeysToMultiSelect: false,
            noUnselect: true,
            columnDefs: [
                {name: 'Name', field: 'name'}
            ]
        };

        $scope.gridOptions.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
        };

        $scope.openNewDialog = function () {
            var modalInstance = $modal.open({
                templateUrl: 'template/currency-modal.html',
                controller: 'CurrencyEditCtrl',
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
                templateUrl: 'template/currency-modal.html',
                controller: 'CurrencyEditCtrl',
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
                controller: 'CurrencyDeleteCtrl',
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

        $scope.gridOptions.data = Currency.query();
    }]);

    currency.controller('CurrencyEditCtrl', ['$scope', '$modalInstance', 'Currency', 'id', function ($scope, $modalInstance, Currency, id) {
        $scope.model = new Currency();
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

        if (id !== null)
            $scope.model.$get();
    }]);

    currency.controller('CurrencyDeleteCtrl', ['$scope', '$modalInstance', 'Currency', 'id', function ($scope, $modalInstance, Currency, id) {
        $scope.entityName = 'currency';

        $scope.ok = function () {
            Currency.delete({id: id}, function () {
                $modalInstance.close();
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
})();