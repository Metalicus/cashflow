(function () {
    var category = angular.module('cashflow-category', []);

    category.controller('CategoryCtrl', ['$scope', 'categoryFactory', '$modal', function ($scope, categoryFactory, $modal) {
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
                templateUrl: 'template/category-modal.html',
                controller: 'CategoryEditCtrl',
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
                templateUrl: 'template/category-modal.html',
                controller: 'CategoryEditCtrl',
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
                controller: 'CategoryDeleteCtrl',
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

        categoryFactory.list(function (data) {
            $scope.gridOptions.data = data;
        });
    }]);

    category.controller('CategoryEditCtrl', ['$scope', '$modalInstance', 'categoryFactory', 'id', function ($scope, $modalInstance, categoryFactory, id) {
        $scope.model = {
            id: id,
            name: null
        };

        $scope.submit = function () {
            categoryFactory.save($scope.model, function (model) {
                $modalInstance.close(model);
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        if (id !== null) {
            categoryFactory.get(id, function (data) {
                $scope.model.type = data.type;
                $scope.model.name = data.name;
            });
        }
    }]);

    category.controller('CategoryDeleteCtrl', ['$scope', '$modalInstance', 'categoryFactory', 'id', function ($scope, $modalInstance, categoryFactory, id) {
        $scope.entityName = 'category';

        $scope.ok = function () {
            operationFactory.del(id, function () {
                $modalInstance.close();
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
})();