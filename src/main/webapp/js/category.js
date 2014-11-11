(function () {
    var category = angular.module('cashflow-category', []);

    category.controller('CategoryCtrl', ['$scope', '$http', function ($scope, $http) {
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
})();