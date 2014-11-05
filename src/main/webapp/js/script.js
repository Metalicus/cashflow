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

cashFlow.controller('categoryController', function($scope, $http) {

    $scope.filterOptions = {
        filterText: "",
        useExternalFilter: true
    };
    $scope.totalServerItems = 0;
    $scope.pagingOptions = {
        pageSizes: [3, 5, 10, 25, 50],
        pageSize: 3,
        currentPage: 1
    };
    $scope.setPagingData = function(data, page, pageSize){
        var pagedData = data.slice((page - 1) * pageSize, page * pageSize);
        $scope.myData = pagedData;
        $scope.totalServerItems = data.length;
        if (!$scope.$$phase) {
            $scope.$apply();
        }
    };
    $scope.getPagedDataAsync = function (pageSize, page, searchText) {
        setTimeout(function () {
            var data;
            if (searchText) {
                var ft = searchText.toLowerCase();
                $http({method: 'GET', url: 'action/category/table', params: {'limit': $scope.pagingOptions.pageSize, 'page': $scope.pagingOptions.currentPage}})
                    .success(function (largeLoad) {
                        data = largeLoad.filter(function(item) {
                            return JSON.stringify(item).toLowerCase().indexOf(ft) != -1;
                        });
                        $scope.setPagingData(data,page,pageSize);
                    });
            } else {
                $http({method: 'GET', url: 'action/category/table', params: {'limit': $scope.pagingOptions.pageSize, 'page': $scope.pagingOptions.currentPage}})
                    .success(function (largeLoad) {
                        $scope.setPagingData(largeLoad,page,pageSize);
                    });
            }
        }, 100);
    };
    $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
    $scope.$watch('pagingOptions', function (newVal, oldVal) {
        if (newVal !== oldVal && newVal.currentPage !== oldVal.currentPage) {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }
    }, true);
    $scope.$watch('pagingOptions.pageSize', function (newVal, oldVal) {
        if (newVal !== oldVal) {
            $scope.pagingOptions.currentPage = 1;// if pageSize changes - start from first page
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }
    }, true);
    $scope.$watch('filterOptions', function (newVal, oldVal) {
        if (newVal !== oldVal) {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);
        }
    }, true);
    $scope.myColumnDefs = [{ field: 'firstName', displayName: 'First Name'},
        { field: 'lastName', displayName: 'Last Name' }];
    $scope.gridOptions = {
        data: 'myData',
        columnDefs: [{field:'name', displayName:'Name'}],
        multiSelect: false,
        selectedItems: [],
        enableSorting: false, // filter and sort should be on the entire data set (server side)
        enableHighlighting: true, // enable copy of text from grid cells
        enablePaging: true,
        showFooter: true,
        totalServerItems: 'totalServerItems',
        pagingOptions: $scope.pagingOptions,
        filterOptions: $scope.filterOptions
    };
});

cashFlow.controller('currencyController', function($scope) {
});

cashFlow.controller('accountController', function($scope) {
});