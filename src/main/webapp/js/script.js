// create the module and name it scotchApp
// also include ngRoute for all our routing needs
var cashFlow = angular.module('cashFlow', ['ngRoute', 'ngGrid']);

// configure our routes
cashFlow.config(function($routeProvider) {
    $routeProvider

        // route for the home page
        .when('/', {
            templateUrl : 'operations.html',
            controller  : 'mainController'
        })

        // route for the category page
        .when('/category', {
            templateUrl : 'category.html',
            controller  : 'categoryController'
        })

        // route for the currency page
        .when('/currency', {
            templateUrl : 'currency.html',
            controller  : 'currencyController'
        })

        // route for the account page
        .when('/account', {
            templateUrl : 'account.html',
            controller  : 'accountController'
        });
});

// create the controller and inject Angular's $scope
cashFlow.controller('mainController', function($scope) {
    $scope.myData = [{name: "Moroni", age: 50},
        {name: "Tiancum", age: 43},
        {name: "Jacob", age: 27},
        {name: "Nephi", age: 29},
        {name: "Enos", age: 34}];
    $scope.gridOptions = { data: 'myData' };
});

cashFlow.controller('categoryController', function($scope) {
    $scope.myData = [{name: "Moroni", age: 51},
        {name: "Tiancum", age: 43},
        {name: "Jacob", age: 27},
        {name: "Nephi", age: 29},
        {name: "Enos", age: 34}];
    $scope.gridOptions = { data: 'myData' };
});

cashFlow.controller('currencyController', function($scope) {
});

cashFlow.controller('accountController', function($scope) {
});