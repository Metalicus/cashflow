(function () {
    var operations = angular.module('cashflow-operations', []);

    // operations type enum
    var OperationType = {
        EXPENSE: 'EXPENSE',
        INCOME: 'INCOME',
        TRANSFER: 'TRANSFER'
    };

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
            enableRowSelection: true,
            enableRowHeaderSelection: false,
            multiSelect: false,
            enableSorting: true,
            modifierKeysToMultiSelect: false,
            noUnselect: true,
            columnDefs: [
                {name: 'Date', field: 'date', type: 'date', cellFilter: 'date:"yyyy-MM-dd"', width: 150},
                {name: 'Type', field: 'type', width: 200},
                {name: 'Account', field: 'account.name', width: 200},
                {name: 'Currency', field: 'currency.name', width: 200},
                {name: 'Amount', field: 'amount', width: 200},
                {name: 'Cross currency amount', field: 'crossCurrency.amount', width: 200},
                {name: 'Exchange rate', field: 'crossCurrency.exchangeRate', width: 200},
                {name: 'Info', field: 'info', width: 200}
            ]
        };

        $scope.gridOptions.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
        };

        $scope.openNewDialog = function () {
            var modalInstance = $modal.open({
                templateUrl: 'template/operation-modal.html',
                controller: 'OperationEditCtrl',
                resolve: {
                    id: function () {
                        return null;
                    }
                }
            });

            modalInstance.result.then(function (model) {
                // add new model to top of the data array
                $scope.gridOptions.data.unshift(model);
            });
        };

        $scope.openEditDialog = function () {
            var selectedRow = $scope.gridApi.selection.getSelectedRows()[0];
            var modalInstance = $modal.open({
                templateUrl: 'template/operation-modal.html',
                controller: 'OperationEditCtrl',
                resolve: {
                    id: function () {
                        return selectedRow["id"];
                    }
                }
            });

            modalInstance.result.then(function (model) {
                // update model in data array
                var rowIndex = $scope.gridApi.grid.rowHashMap.get(selectedRow).i;
                $scope.gridOptions.data[rowIndex] = model;
            });
        };

        $scope.openDeleteDialog = function () {
            var selectedRow = $scope.gridApi.selection.getSelectedRows()[0];
            var modalInstance = $modal.open({
                templateUrl: 'template/delete-modal.html',
                controller: 'OperationDeleteCtrl',
                resolve: {
                    id: function () {
                        return selectedRow["id"];
                    }
                }
            });

            modalInstance.result.then(function () {
                var rowIndex = $scope.gridApi.grid.rowHashMap.get(selectedRow).i;
                $scope.gridOptions.data.splice(rowIndex, 1);
            });
        };

        operationFactory.list(function (data) {
            $scope.gridOptions.data = data;
        });
    }]);

    // controller for model type tabs. Hold all tab operations, like select current, etc.
    operations.controller('TypeTabController', ['$scope', function ($scope) {
        $scope.tabs = [
            {type: OperationType.EXPENSE, active: true},
            {type: OperationType.TRANSFER, active: false},
            {type: OperationType.INCOME, active: false}
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
                type: OperationType.EXPENSE,
                date: new Date(),
                amount: null,
                moneyWas: 0,
                moneyBecome: 0,
                account: null,
                currency: null,
                category: null,
                info: '',
                calcMoneyBecome: function () { // function to calculate money become
                    var moneyWas = parseFloat($scope.model.moneyWas);
                    var money = parseFloat($scope.model.amount);

                    if (this.type === OperationType.EXPENSE) {
                        this.moneyBecome = (moneyWas - money).toFixed(2);
                    } else {
                        this.moneyBecome = (moneyWas + money).toFixed(2);
                    }
                }
            };

            $scope.changeAccount = function (model) {
                $scope.model.moneyWas = model["balance"];
                $timeout(function () {
                    // due to the fact that the event is triggered before the model is selected, delay the money update
                    $scope.moneyUpdate();
                }, 100);
            };

            $scope.moneyUpdate = function () {
                if (!$scope.model.currency) return; // do nothing if currency is not set
                if (!$scope.model.account) return; // do nothing if account is not set
                if (!$scope.model.moneyWas) return; // do nothing if moneyWas is empty

                if ($scope.model.currency.id == $scope.model.account.currency.id) {
                    // if operation currency is equal to account currency we can calculate
                    $scope.model.calcMoneyBecome();
                }
            };

            $scope.isTransfer = function () {
                return $scope.model.type === OperationType.TRANSFER;
            };

            $scope.submit = function () {
                if ($scope.model.type !== OperationType.TRANSFER && $scope.model.transfer) {
                    delete $scope.model["transfer"];
                }

                operationFactory.save($scope.model, function (model) {
                    $modalInstance.close(model);
                });
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

                    if (data.crossCurrency)
                        $scope.model.crossCurrency = data.crossCurrency;
                    if (data.transfer)
                        $scope.model.transfer = data.transfer;
                });
            }
        }]);

    // controller for deleting operations
    operations.controller('OperationDeleteCtrl', ['$scope', '$modalInstance', 'operationFactory', 'id', function ($scope, $modalInstance, operationFactory, id) {
        $scope.entityName = 'operation';

        $scope.ok = function () {
            operationFactory.del(id, function () {
                $modalInstance.close();
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
})();