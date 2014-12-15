(function () {
    var category = angular.module('cashflow-category', []);

    category.controller('CategoryCtrl', ['$scope', 'Category', '$modal', '$filter', function ($scope, Category, $modal, $filter) {
        $scope.gridOptions = {
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            enableSorting: true,
            modifierKeysToMultiSelect: false,
            noUnselect: true,
            columnDefs: [
                {name: $filter('translate')('category.table.name'), enableColumnMenu: false, field: 'name'}
            ],
            onRegisterApi: function (gridApi) {
                $scope.gridApi = gridApi;
            },
            serverData: {
                source: Category
            }
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
    }]);

    category.controller('CategoryEditCtrl', ['$scope', '$modalInstance', 'Category', 'id', function ($scope, $modalInstance, Category, id) {
        $scope.model = new Category();
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

    category.controller('CategoryDeleteCtrl', ['$scope', '$modalInstance', 'Category', 'id', function ($scope, $modalInstance, Category, id) {
        $scope.entityName = 'category';

        $scope.ok = function () {
            Category.delete({id: id}, function () {
                $modalInstance.close();
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
})();