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
    operations.controller('OperationsCtrl', ['$scope', 'uiGridConstants', 'Operation', 'Category', '$modal', '$filter',
        function ($scope, uiGridConstants, Operation, Category, $modal, $filter) {

            // filters
            $scope.categories = Category.query();

            // some magic here. i want to feel screen with operation table, because it's huge, but i want it only if
            // windows width is bigger than sum of columns width
            var defaultWidth = 160;
            if (window.innerWidth && window.innerWidth > (defaultWidth * 9)) {
                defaultWidth = window.innerWidth / 9;
            }

            //console.debug('column default size is: ' + defaultWidth);
            //console.debug('windows width is: ' + window.innerWidth);

            $scope.gridOptions = {
                enableRowSelection: true,
                enableRowHeaderSelection: false,
                multiSelect: false,
                useExternalSorting: true,
                modifierKeysToMultiSelect: false,
                infiniteScrollRowsFromEnd: 20,
                infiniteScrollDown: true,
                noUnselect: true,
                columnDefs: [
                    {
                        name: $filter('translate')('operations.table.date'),
                        field: 'date',
                        type: 'date',
                        cellFilter: 'date:"yyyy-MM-dd"',
                        width: defaultWidth,
                        enableColumnMenu: false,
                        defaultSort: {direction: uiGridConstants.DESC}
                    },
                    {
                        name: $filter('translate')('operations.table.type'),
                        field: 'type',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.category'),
                        field: 'category.name',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.account'),
                        field: 'account.name',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.currency'),
                        field: 'currency.name',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.amount'),
                        field: 'amount',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.crossCurrencyAmount'),
                        field: 'crossCurrency.amount',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.exchangeRate'),
                        field: 'crossCurrency.exchangeRate',
                        width: defaultWidth,
                        enableColumnMenu: false
                    },
                    {
                        name: $filter('translate')('operations.table.info'),
                        field: 'info',
                        width: defaultWidth,
                        enableColumnMenu: false
                    }
                ],
                onRegisterApi: function (gridApi) {
                    $scope.gridApi = gridApi;
                },
                serverData: {
                    source: Operation
                }
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

            // todo: remove array modifiing
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
        }]);

    // controller for model type tabs. Hold all tab operations, like select current, etc.
    operations.controller('TypeTabController', ['$scope', 'OPERATION_TYPE', function ($scope, OPERATION_TYPE) {
        $scope.tabs = [
            {type: OPERATION_TYPE.EXPENSE, active: true},
            {type: OPERATION_TYPE.TRANSFER, active: false},
            {type: OPERATION_TYPE.INCOME, active: false}
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
    operations.controller('OperationEditCtrl', ['$scope', '$modalInstance', 'OPERATION_TYPE', 'Operation', 'Account', 'Currency', 'Category', '$timeout', 'id',
        function ($scope, $modalInstance, OPERATION_TYPE, Operation, Account, Currency, Category, $timeout, id) {

            $scope.showCalculator = false;
            $scope.toggleCalculator = function () {
                $scope.showCalculator = !$scope.showCalculator;
            };

            $scope.model = new Operation();
            $scope.model.id = id;
            $scope.model.date = new Date();
            $scope.model.type = OPERATION_TYPE.EXPENSE;

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
                    var moneyWas = parseFloat($scope.model.moneyWas);
                    var money = parseFloat($scope.model.amount);

                    if ($scope.model.type === OPERATION_TYPE.EXPENSE || $scope.model.type === OPERATION_TYPE.TRANSFER) {
                        $scope.model.moneyBecome = (moneyWas - money).toFixed(2);
                    } else {
                        $scope.model.moneyBecome = (moneyWas + money).toFixed(2);
                    }
                }
            };

            $scope.isTransfer = function () {
                return $scope.model.type === OPERATION_TYPE.TRANSFER;
            };

            $scope.submit = function () {
                if ($scope.model.type !== OPERATION_TYPE.TRANSFER && $scope.model["transfer"]) {
                    delete $scope.model["transfer"];
                }

                if (id !== null) {
                    $scope.model.$update(function (model) {
                        $modalInstance.close(model);
                    });
                } else {
                    $scope.model.$save(function (model) {
                        $modalInstance.close(model);
                    });
                }
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

            $scope.accounts = Account.query();
            $scope.currencies = Currency.query();
            $scope.categories = Category.query();

            if (id !== null) {
                $scope.model.$get(function () {
                    $scope.model.date = new Date($scope.model.date);
                });
            }
        }]);

    // controller for deleting operations
    operations.controller('OperationDeleteCtrl', ['$scope', '$modalInstance', 'Operation', 'id', function ($scope, $modalInstance, Operation, id) {
        $scope.entityName = 'operation';

        $scope.ok = function () {
            Operation.delete({id: id}, function () {
                $modalInstance.close();
            });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    }]);
})();