(function () {
    var category = angular.module('cashflow-category', []);

    category.controller('CategoryCtrl', ['$scope', 'categoryFactory', function ($scope, categoryFactory) {
        $scope.gridOptions = {
            enableSorting: true,
            columnDefs: [
                {name: 'Name', field: 'name'}
            ]
        };

        categoryFactory.list(function (data) {
            $scope.gridOptions.data = data;
        });
    }]);
})();