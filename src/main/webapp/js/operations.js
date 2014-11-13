(function () {
    var operations = angular.module('cashflow-operations', []);

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
    operations.controller('OperationsCtrl', ['$scope', 'operationFactory', '$modal', function ($scope, operationFactory, $modal) {
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
                {name: 'Info', field: 'info'}
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

        operationFactory.list(function (data) {
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
    operations.controller('OperationEditCtrl', ['$scope', '$modalInstance', 'operationFactory', 'accountFactory', 'currencyFactory', 'categoryFactory', '$timeout', 'id',
        function ($scope, $modalInstance, operationFactory, accountFactory, currencyFactory, categoryFactory, $timeout, id) {
            // operation model
            $scope.model = {
                id: id,
                type: 'OUTCOME',
                date: new Date(),
                amount: 0,
                moneyWas: 0,
                moneyBecome: 0,
                account: null,
                currency: null,
                category: null,
                info: ''
            };

            $scope.changeAccount = function (model) {
                $scope.model.moneyWas = model.balance;
                $timeout(function () {
                    // because of on-select fires before set the selected value to model we need to little timeout before performig update
                    $scope.moneyUpdate();
                }, 100);
            };

            $scope.moneyUpdate = function () {
                if (!$scope.model.currency) return; // do nothing if currency is not set
                if (!$scope.model.account) return; // do nothing if account is not set
                if (!$scope.model.moneyWas) return; // do nothing if moneyWas is empty

                if ($scope.model.currency.id == $scope.model.account.currency.id) {
                    // if operation currency is equal to account currency we can calculate
                    if ($scope.model.type === 'OUTCOME') {
                        $scope.model.moneyBecome = (parseFloat($scope.model.moneyWas) - parseFloat($scope.model.amount)).toFixed(2);
                    } else if ($scope.model === 'INCOME') {
                        $scope.model.moneyBecome = (parseFloat($scope.model.moneyWas) + parseFloat($scope.model.amount)).toFixed(2);
                    }
                }
            };

            $scope.submit = function () {
                $modalInstance.dismiss('ok');
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

            accountFactory.list(function (data) {
                $scope.accounts = data;
            });
            currencyFactory.list(function (date) {
                $scope.currencies = date;
            });
            categoryFactory.list(function (date) {
                $scope.categories = date;
            });

            if (id !== null) {
                operationFactory.get(id, function (data) {
                    $scope.model.type = data.type;
                    $scope.model.date = new Date(data.date);
                    $scope.model.amount = data.amount;
                    $scope.model.moneyWas = data.moneyWas;
                    $scope.model.moneyBecome = data.moneyBecome;
                    $scope.model.account = data.account;
                    $scope.model.currency = data.currency;
                    $scope.model.category = data.category;
                    $scope.model.info = data.info;
                });
            }
        }]);
})();