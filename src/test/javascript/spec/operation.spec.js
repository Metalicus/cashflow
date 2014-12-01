'use strict';

describe('Operation tests ', function () {

    beforeEach(module('cashFlow'));

    describe('OperationsCtrl', function () {
        var $scope, $modal, modalInstance, $httpBackend, controller, deferred;

        beforeEach(inject(function (_$controller_, _$httpBackend_, _$q_) {
            $scope = {};
            $httpBackend = _$httpBackend_;

            // expecting request for category loading for filter
            $httpBackend.expectGET('action/category').respond('{"content":[] }');

            $scope.gridOptions = {};

            deferred = _$q_.defer();
            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: deferred.promise
            };

            $modal = {
                open: jasmine.createSpy('modal.open').and.returnValue(modalInstance)
            };

            controller = _$controller_('OperationsCtrl', {
                $scope: $scope,
                $modal: $modal
            });
        }));

        it('should create default parameters', function () {
            $httpBackend.flush();

            // grid
            expect($scope.gridOptions.columnDefs).not.toBeUndefined();
            expect($scope.gridOptions.serverData).not.toBeUndefined();
            expect($scope.gridOptions.onRegisterApi).not.toBeUndefined();

            // modal methods
            expect($scope.openNewDialog).not.toBeUndefined();
            expect($scope.openEditDialog).not.toBeUndefined();
            expect($scope.openDeleteDialog).not.toBeUndefined();
        });

        it('should call modal dialog and add new model', function () {
            $httpBackend.flush();

            spyOn(deferred.promise, 'then').and.callFake(function (callback) {
                return callback({id: 3});
            });

            $scope.gridOptions.data = [
                {id: 1},
                {id: 2}
            ];
            expect($scope.gridOptions.data.length).toEqual(2);

            $scope.openNewDialog();
            expect($modal.open).toHaveBeenCalled();
            expect($scope.gridOptions.data.length).toEqual(3);

            expect($scope.gridOptions.data[0]['id']).toEqual(3);
            expect($scope.gridOptions.data[1]['id']).toEqual(1);
            expect($scope.gridOptions.data[2]['id']).toEqual(2);
        });

        it('should call modal dialog and edit existing model', function () {
            $httpBackend.flush();

            $scope.gridApi = {
                selection: {
                    getSelectedRows: function () {
                        return [{id: 1, name: 'old name 1'}]
                    }
                },
                grid: {
                    rowHashMap: {
                        get: function () {
                            return {
                                i: 1
                            }
                        }
                    }
                }
            };

            spyOn(deferred.promise, 'then').and.callFake(function (callback) {
                return callback({id: 1, name: 'new name 1'});
            });

            $scope.gridOptions.data = [
                {id: 3, name: 'old name 3'},
                {id: 1, name: 'old name 1'},
                {id: 2, name: 'old name 2'}
            ];
            expect($scope.gridOptions.data.length).toEqual(3);

            $scope.openEditDialog();
            expect($modal.open).toHaveBeenCalled();
            expect($scope.gridOptions.data.length).toEqual(3);
            expect($scope.gridOptions.data[0]['name']).toEqual('old name 3');
            expect($scope.gridOptions.data[1]['name']).toEqual('new name 1');
            expect($scope.gridOptions.data[2]['name']).toEqual('old name 2');
        });

        it('should call modal dialog and delet existing model', function () {
            $httpBackend.flush();

            $scope.gridApi = {
                selection: {
                    getSelectedRows: function () {
                        return [{id: 1, name: 'old name 1'}]
                    }
                },
                grid: {
                    rowHashMap: {
                        get: function () {
                            return {
                                i: 1
                            }
                        }
                    }
                }
            };

            spyOn(deferred.promise, 'then').and.callFake(function (callback) {
                return callback();
            });

            $scope.gridOptions.data = [
                {id: 3},
                {id: 1},
                {id: 2}
            ];
            expect($scope.gridOptions.data.length).toEqual(3);

            $scope.openDeleteDialog();
            expect($modal.open).toHaveBeenCalled();
            expect($scope.gridOptions.data.length).toEqual(2);
            expect($scope.gridOptions.data[0]['id']).toEqual(3);
            expect($scope.gridOptions.data[1]['id']).toEqual(2);
        });
    });

    describe('OperationEditCtrl: new', function () {
        var $scope, OPERATION_TYPE, controller, modalInstance, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_, _OPERATION_TYPE_) {
            $scope = {};
            $httpBackend = _$httpBackend_;
            OPERATION_TYPE = _OPERATION_TYPE_;

            // loading of all account for select
            $httpBackend
                .expectGET('action/account').respond('{"content":[] }');
            // loading all currencies for select
            $httpBackend
                .expectGET('action/currency').respond('{"content":[] }');
            // loading all categories for select
            $httpBackend
                .expectGET('action/category').respond('{"content":[] }');

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('OperationEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: null
            });
        }));

        it('should create new Operation with default values', function () {
            // we don't expect new requests
            $httpBackend.flush();

            // only known values
            expect($scope.model.id).toEqual(null);
            expect($scope.model.date).not.toBe(null);
            expect($scope.model.type).toEqual(OPERATION_TYPE.EXPENSE);

            // rest of then are undefined
            expect($scope.model.moneyWas).toBeUndefined();
            expect($scope.model.moneyWas).toBeUndefined();
            expect($scope.model.account).toBeUndefined();
            expect($scope.model.currency).toBeUndefined();
            expect($scope.model.category).toBeUndefined();
            expect($scope.model.amount).toBeUndefined();
            expect($scope.model.crossCurrency).toBeUndefined();
            expect($scope.model.transfer).toBeUndefined();
        });

        it('pressed save button', function () {
            // we expect only insert request
            $httpBackend.expectPOST('action/operation').respond({id: 1});

            $scope.submit();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('OperationEditCtrl: edit', function () {
        var $scope, OPERATION_TYPE, controller, modalInstance, $httpBackend, $timeout;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_, _OPERATION_TYPE_, _$timeout_) {
            $scope = {};
            $httpBackend = _$httpBackend_;
            $timeout = _$timeout_;
            OPERATION_TYPE = _OPERATION_TYPE_;

            // loading of all account for select
            $httpBackend
                .expectGET('action/account').respond('{"content":[] }');
            // loading all currencies for select
            $httpBackend
                .expectGET('action/currency').respond('{"content":[] }');
            // loading all categories for select
            $httpBackend
                .expectGET('action/category').respond('{"content":[] }');

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('OperationEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                $timeout: $timeout,
                id: 1
            });
        }));

        it('should create new Operation with default values', function () {
            // only known values
            expect($scope.model.id).toEqual(1);
            expect($scope.model.date).not.toBe(null);
            expect($scope.model.type).toEqual(OPERATION_TYPE.EXPENSE);

            // rest of then are undefined
            expect($scope.model.moneyWas).toBeUndefined();
            expect($scope.model.moneyWas).toBeUndefined();
            expect($scope.model.account).toBeUndefined();
            expect($scope.model.currency).toBeUndefined();
            expect($scope.model.category).toBeUndefined();
            expect($scope.model.amount).toBeUndefined();
            expect($scope.model.crossCurrency).toBeUndefined();
            expect($scope.model.transfer).toBeUndefined();
        });

        it('should return correct answer if this operation is TRANSFER', function () {
            expect($scope.model.type).toEqual(OPERATION_TYPE.EXPENSE);
            expect($scope.isTransfer()).toBe(false);

            $scope.model.type = OPERATION_TYPE.TRANSFER;
            expect($scope.isTransfer()).toBe(true);
        });

        it('should load Operation then id specified', function () {
            $httpBackend
                .expectGET('action/operation/1')
                .respond({
                    id: 1,
                    date: new Date(),
                    account: {id: 1, name: 'test account', currency: {id: 1, name: 'test currency'}, balance: 10.00},
                    currency: {id: 1, name: 'test currency'},
                    category: {id: 1, name: 'test category'},
                    type: OPERATION_TYPE.EXPENSE,
                    amount: 3.00,
                    moneyWas: 10.00,
                    moneyBecome: 7.00,
                    info: 'beer'
                });
            $httpBackend.flush();

            // load data from server
            expect($scope.model.id).toEqual(1);
            expect($scope.model.date).not.toBe(null);
            expect($scope.model.type).toEqual(OPERATION_TYPE.EXPENSE);
            expect($scope.model.moneyWas).toEqual(10.00);
            expect($scope.model.moneyBecome).toEqual(7.00);
            expect($scope.model.account).toEqual({
                id: 1,
                name: 'test account',
                currency: {id: 1, name: 'test currency'},
                balance: 10.00
            });
            expect($scope.model.currency).toEqual({id: 1, name: 'test currency'});
            expect($scope.model.category).toEqual({id: 1, name: 'test category'});
            expect($scope.model.amount).toEqual(3.00);

            // this still undefined
            expect($scope.model.crossCurrency).toBeUndefined();
            expect($scope.model.transfer).toBeUndefined();
        });

        it('should update money', function () {
            $httpBackend
                .expectGET('action/operation/1')
                .respond({
                    id: 1,
                    date: new Date(),
                    type: OPERATION_TYPE.EXPENSE,
                    amount: 3.00,
                    moneyBecome: 7.00,
                    info: 'beer',
                    category: {id: 1, name: 'test category'}
                });
            $httpBackend.flush();

            // if account and currency and moneyWas not specified nothing is happened
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual(7.00);

            // if specified only account nothing is happened
            $scope.model.account = {id: 1, name: 'test account', currency: {id: 1, name: 'EUR'}, balance: 10.00};
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual(7.00);

            // if spicified account and currency but not a moneyWas nothing is happened
            $scope.model.currency = {id: 2, name: 'USD'};
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual(7.00);

            // if all fields are specified but currency is not the same nothing is happened
            $scope.model.moneyWas = 10.00;
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual(7.00);

            // same currencies
            $scope.model.currency = $scope.model.account.currency;

            // check expense
            $scope.model.amount = 1;
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual('9.00');

            // check transfer
            $scope.type = OPERATION_TYPE.TRANSFER;
            $scope.model.amount = 7;
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual('3.00');

            // check income
            $scope.model.type = OPERATION_TYPE.INCOME;
            $scope.model.amount = 4;
            $scope.moneyUpdate();
            expect($scope.model.moneyBecome).toEqual('14.00');

            // performe account change
            var newAccount = {id: 2, name: 'test account 2', currency: {id: 1, name: 'EUR'}, balance: 99.00};
            $scope.model.type = OPERATION_TYPE.EXPENSE;
            $scope.model.amount = '21.00';
            $scope.model.account = newAccount;
            $scope.changeAccount(newAccount);
            $timeout.flush();
            expect($scope.model.moneyBecome).toEqual('78.00');
        });

        it('should change account', function () {
            $scope.model = {
                id: null,
                date: new Date(),
                type: OPERATION_TYPE.EXPENSE,
                amount: 3.00,
                moneyBecome: 7.00,
                info: 'beer',
                category: {id: 1, name: 'test category'},
                account: {id: 1, name: 'test account', currency: {id: 1, name: 'EUR'}, balance: 10.00}
            };

            var newAccount = {id: 2, name: 'test account 2', currency: {id: 1, name: 'EUR'}, balance: 99.00};
            // actual model seting will perform ui-select
            $scope.model.account = newAccount;
            $scope.changeAccount(newAccount);
            expect($scope.model.moneyWas).toEqual(99.00);
        });

        it('pressed cancel button', function () {
            // we expect only this
            $httpBackend
                .expectGET('action/operation/1')
                .respond({
                    id: 1,
                    date: new Date(),
                    type: OPERATION_TYPE.EXPENSE,
                    amount: 3.00,
                    moneyBecome: 7.00,
                    info: 'beer',
                    category: {id: 1, name: 'test category'}
                });
            $httpBackend.flush();

            $scope.cancel();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('cancel');
        });

        it('pressed save button', function () {
            // we expect get and save requests
            $httpBackend
                .expectGET('action/operation/1')
                .respond({
                    id: 1,
                    date: new Date(),
                    type: OPERATION_TYPE.EXPENSE,
                    amount: 3.00,
                    moneyBecome: 7.00,
                    info: 'beer',
                    category: {id: 1, name: 'test category'}
                });
            $httpBackend.expectPUT('action/operation/1').respond({id: 1});

            $scope.submit();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('OperationDeleteCtrl', function () {
        var $scope, $httpBackend, modalInstance, controller;

        beforeEach(inject(function (_$controller_, _$httpBackend_) {
            $scope = {};
            $httpBackend = _$httpBackend_;

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('OperationDeleteCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: 1
            });
        }));

        it('pressed cancel', function () {
            expect($scope.entityName).toEqual('operation');
            $scope.cancel();
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.verifyNoOutstandingRequest();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('cancel');
        });

        it('pressed ok', function () {
            $httpBackend.expectDELETE('action/operation/1').respond();
            expect($scope.entityName).toEqual('operation');
            $scope.ok();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('TypeTabController', function () {
        var $scope, $httpBackend, controller, OPERATION_TYPE;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_, _OPERATION_TYPE_) {
            $scope = {};
            $httpBackend = _$httpBackend_;
            OPERATION_TYPE = _OPERATION_TYPE_;

            controller = _$controller_('TypeTabController', {
                $scope: $scope
            });
        }));

        it('should create default tabs map', function () {
            expect($scope.tabs[0].type).toEqual(OPERATION_TYPE.EXPENSE);
            expect($scope.tabs[0].active).toEqual(true);

            expect($scope.tabs[1].type).toEqual(OPERATION_TYPE.TRANSFER);
            expect($scope.tabs[1].active).toEqual(false);

            expect($scope.tabs[2].type).toEqual(OPERATION_TYPE.INCOME);
            expect($scope.tabs[2].active).toEqual(false);
        });

        it('should return current type', function () {
            expect($scope.getCurrentType()).toEqual(OPERATION_TYPE.EXPENSE);

            $scope.tabs[0].active = false;
            $scope.tabs[1].active = true;
            expect($scope.getCurrentType()).toEqual(OPERATION_TYPE.TRANSFER);

            $scope.tabs[1].active = false;
            $scope.tabs[2].active = true;
            expect($scope.getCurrentType()).toEqual(OPERATION_TYPE.INCOME);
        });

        it('should select tab', function () {
            expect($scope.tabs[0].active).toEqual(true);
            $scope.selTab(OPERATION_TYPE.EXPENSE);
            expect($scope.tabs[0].active).toEqual(true);
            expect($scope.tabs[1].active).toEqual(false);
            expect($scope.tabs[2].active).toEqual(false);

            $scope.selTab(OPERATION_TYPE.TRANSFER);
            expect($scope.tabs[0].active).toEqual(false);
            expect($scope.tabs[1].active).toEqual(true);
            expect($scope.tabs[2].active).toEqual(false);

            $scope.selTab(OPERATION_TYPE.INCOME);
            expect($scope.tabs[0].active).toEqual(false);
            expect($scope.tabs[1].active).toEqual(false);
            expect($scope.tabs[2].active).toEqual(true);
        });
    });

    describe('directive: typeTabs', function () {
        var $scope, $compile;

        beforeEach(module('cashFlow'));

        beforeEach(inject(function (_$rootScope_, _$compile_) {
            $compile = _$compile_;
            $scope = _$rootScope_.$new();

            $scope.value = "firstValue";
            $scope.selTab = function () {
            };
            $scope.getCurrentType = function () {
            };

            spyOn($scope, 'selTab');
            spyOn($scope, 'getCurrentType');
        }));

        it('should ignore if ng-model is not specified', function () {
            // ng-model is requirement
            function errorWrapper() {
                $compile('<tabset type-tabs></tabset>')($scope);
                $scope.$digest();
            }

            expect(errorWrapper).toThrow();
        });

        it('should watch for type changes', function () {
            $compile('<tabset type-tabs ng-model="value"></tabset>')($scope);
            $scope.$digest();
            expect($scope.selTab).toHaveBeenCalledWith("firstValue");

            // this can't be done with usual testing, it should go to protractor
            // $scope.value.$setViewValue = "secondValue";
            // expect($scope.selTab).toHaveBeenCalledWith("secondValue");

            // end click also should go to protractor
        });

    });
});