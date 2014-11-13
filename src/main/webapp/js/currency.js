(function () {
    var currency = angular.module('cashflow-currency', []);

    currency.controller('CurrencyCtrl', ['$scope', 'currencyFactory', function ($scope, currencyFactory) {
        $scope.gridOptions = {
            enableSorting: true,
            columnDefs: [
                {name: 'Name', field: 'name'}
            ]
        };

        currencyFactory.list(function (data) {
            $scope.gridOptions.data = data;
        });
    }]);
})();