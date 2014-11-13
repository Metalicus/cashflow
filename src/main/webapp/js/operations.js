(function () {
    var operations = angular.module('cashflow-operations', []);

    operations.directive('selectAccount', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/select-account.html'
        }
    });

    operations.directive('selectCurrency', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/select-currency.html'
        }
    });

    operations.directive('selectCategory', function () {
        return {
            restrict: 'E',
            templateUrl: 'template/select-category.html'
        }
    });

    operations.directive('typeTabs', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {
                if (!ngModel) return; // do nothing if no ng-model

                // select default tab
                scope.selTab(ngModel.$viewValue);

                element.on("click", function () {
                    var currentType = scope.getCurrentType();
                    if (currentType !== ngModel.$viewValue) { // update only if new value
                        ngModel.$setViewValue(currentType);
                    }
                });

                ngModel.$render = function () { // select new tab then model is changed
                    scope.selTab(ngModel.$viewValue);
                };
            }
        }
    });

    // controller for datepicker, to hold options and open event
    operations.controller('DPickerCtrl', ['$scope', function ($scope) {
        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
        $scope.format = $scope.formats[0];
    }]);

    // controller for operation table and modal dialogs calls
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

            gridApi.cellNav.on.navigate($scope, function (newRowCol) {
                $scope.gridApi.selection.selectRow(newRowCol.row.entity);
                $scope.disabled = $scope.gridApi.selection.getSelectedRows().length === 0;
            });
        };

        $scope.openNewDialog = function () {
            $modal.open({
                templateUrl: 'template/operation-modal.html',
                controller: 'OperationEditCtrl',
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
                    controller: 'OperationEditCtrl',
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

    // controller for model type tabs. Hold all tab operations, like select current, etc.
    operations.controller('TypeTabController', ['$scope', function ($scope) {
        $scope.tabs = [
            {type: 'OUTCOME', disabled: false, active: true},
            {type: 'TRANSFER', disabled: false, active: false},
            {type: 'INCOME', disabled: false, active: false}
        ];

        $scope.getCurrentType = function () {
            var currentType = null;
            for (var i = 0; i < $scope.tabs.length; i++) {
                if ($scope.tabs[i].active) {
                    currentType = $scope.tabs[i].type;
                    break;
                }
            }

            return currentType;
        };

        $scope.selTab = function (type) {
            for (var i = 0; i < $scope.tabs.length; i++) {
                $scope.tabs[i].active = type === $scope.tabs[i].type;
            }
        };
    }]);

    // controller for operation edit or add. Hold operation model
    operations.controller('OperationEditCtrl', ['$scope', '$modalInstance', '$http', 'id', function ($scope, $modalInstance, $http, id) {
        // operation model
        $scope.model = {
            id: id,
            type: 'OUTCOME',
            date: new Date(),
            amount: null,
            moneyWas: null,
            moneyBecome: null,
            account: {},
            currency: {},
            category: {}
        };

        // ui-select models
        $scope.account = {};
        $scope.currency = {};
        $scope.category = {};

        $scope.updateMoney = function () {

        };

        $scope.accountSelect = function (model) {
            $scope.model.account = model;
            $scope.model.moneyWas = model["balance"];
        };

        $scope.categorySelect = function (model) {
            $scope.model.category = model;
        };

        $scope.currencySelect = function (model) {
            $scope.model.currency = model;
        };

        $scope.submit = function () {
            $modalInstance.dismiss('ok');
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };

        $http.get('action/account/list')
            .then(function (response) {
                $scope.accounts = response.data;
            });

        $http.get('action/currency/list')
            .then(function (response) {
                $scope.currencies = response.data;
            });

        $http.get('action/category/list')
            .then(function (response) {
                $scope.categories = response.data;
            });

        if (id !== null) {
            $http.get('action/operation/get/' + id)
                .success(function (data) {
                    //model
                    $scope.model.type = data.type;
                    $scope.model.date = new Date(data.date);
                    $scope.model.amount = data.amount;
                    $scope.model.moneyWas = data.moneyWas;
                    $scope.model.moneyBecome = data.moneyBecome;
                    $scope.model.account = data.account;
                    $scope.model.currency = data.currency;
                    $scope.model.category = data.category;

                    //ui-select
                    $scope.account.selected = data.account;
                    $scope.currency.selected = data.currency;
                    $scope.category.selected = data.category;
                });
        }
    }]);
})();