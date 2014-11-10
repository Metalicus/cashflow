var cashFlow = angular.module('cashFlow', ['ngRoute', 'ui.grid', 'ui.grid.selection','ui.grid.cellNav']);

// configure our routes
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

cashFlow.controller('mainController', ['$scope', '$http', function ($scope, $http) {
    $scope.gridOptions = {
        enableRowHeaderSelection: false,
        multiSelect: false,
        enableSorting: true,
        columnDefs: [
            {name: 'Date', field: 'date', type: 'date', cellFilter: 'date:"yyyy-MM-dd"'},
            {name: 'Account', field: 'account.name'},
            {name: 'Currency', field: 'currency.name'},
            {name: 'Amount', field: 'amount'}
        ]
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