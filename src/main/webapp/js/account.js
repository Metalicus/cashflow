(function () {
    var account = angular.module('cashflow-account', []);

    account.controller('AccountCtrl', ['$scope', 'accountFactory', function ($scope, accountFactory) {
        $scope.gridOptions = {
            enableSorting: true,
            columnDefs: [
                {name: 'Name', field: 'name'},
                {name: 'Currency', field: 'currency.name'},
                {name: 'Current balance', field: 'balance'}
            ]
        };

        accountFactory.list(function (data) {
            $scope.gridOptions.data = data;
        });
    }]);
})();