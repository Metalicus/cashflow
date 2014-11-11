(function () {
    var operations = angular.module('cashflow-operations', []);

    operations.controller('OperationsCtrl', ['$scope', '$http', '$modal', function ($scope, $http, $modal) {
        $scope.gridOptions = {
            enableRowHeaderSelection: false,
            multiSelect: false,
            enableSorting: true,
            columnDefs: [
                {name: 'Date', field: 'date', type: 'date', cellFilter: 'date:"yyyy-MM-dd"'},
                {name: 'Type', field: 'type'},
                {name: 'Account', field: 'account.name'},
                {name: 'Currency', field: 'currency.name'},
                {name: 'Amount', field: 'amount'},
                {name: 'Cross currency amount', field: 'crossCurrency.amount'},
                {name: 'Exchange rate', field: 'crossCurrency.exchangeRate'},
                {name: 'Info', filed: 'info'}
            ]
        };

        $scope.disabled = true;

        $scope.gridOptions.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;

            gridApi.cellNav.on.navigate($scope, function (newRowCol, oldRowCol) {
                $scope.gridApi.selection.selectRow(newRowCol.row.entity);
                $scope.disabled = $scope.gridApi.selection.getSelectedRows().length === 0;
            });
        };

        $scope.openNewDialog = function () {
            $modal.open({
                templateUrl: 'template/operation-modal.html',
                controller: 'OperationModelCtrl',
                resolve: {
                    id: function () {
                        return null;
                    }
                }
            });
        };

        $scope.openEditDialog = function () {
            if ($scope.gridApi.selection.getSelectedRows().length > 0) {
                $modal.open({
                    templateUrl: 'template/operation-modal.html',
                    controller: 'OperationModelCtrl',
                    resolve: {
                        id: function () {
                            return $scope.gridApi.selection.getSelectedRows()[0]["id"];
                        }
                    }
                });
            }
        };

        $http.get('action/operation/list')
            .success(function (data) {
                for (var i = 0; i < data.length; i++) {
                    data[i].date = new Date(data[i].date);
                }
                $scope.gridOptions.data = data;
            });
    }]);

    operations.controller('OperationModelCtrl', ['$scope', '$modalInstance', '$http', 'id', function ($scope, $modalInstance, $http, id) {
        $scope.id = id;
        $scope.type = 'OUTCOME';
        $scope.date = new Date();
        $scope.amount = "";
        $scope.account = {};

        $scope.tabs = [
            {type: 'OUTCOME', disabled: false, active: true},
            {type: 'TRANSFER', disabled: false, active: false},
            {type: 'INCOME', disabled: false, active: false}
        ];

        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.setType = function (index) {
            if ($scope.id === null) {
                $scope.type = $scope.tabs[index].type;

                for (var i = 0; i < $scope.tabs.length; i++)
                    $scope.tabs[i].active = false;

                $scope.tabs[index].active = true;
            }
        };

        $scope.isActived = function (index) {
            return $scope.tabs[index].active;
        };

        $scope.isTabDisabled = function (index) {
            return $scope.tabs[index].disabled;
        };

        $scope.datePickerOpen = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        $scope.ok = function () {
            $modalInstance.dismiss('ok');
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        if (id !== null) {

            $http.get('action/account/list')
                .then(function (response) {
                    $scope.accounts = response.data;
                });

            $http.get('action/operation/get/' + id)
                .success(function (data) {
                    $scope.type = data.type;
                    $scope.date = new Date(data.date);
                    $scope.amount = data.amount;
                    $scope.account.selected = data.account;

                    for (var i = 0; i < $scope.tabs.length; i++) {
                        if ($scope.tabs[i].type === data.type) {
                            $scope.tabs[i].disabled = false;
                            $scope.tabs[i].active = true;
                        } else {
                            $scope.tabs[i].disabled = true;
                            $scope.tabs[i].active = false;
                        }
                    }
                });
        }
    }]);

})();