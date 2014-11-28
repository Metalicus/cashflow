'use strict';

describe('Operation Tests ', function () {

    beforeEach(module('cashFlow'));

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
        var $scope, OPERATION_TYPE, controller, $controller, modalInstance, $httpBackend, $timeout;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_, _OPERATION_TYPE_, _$timeout_) {
            $scope = {};
            $controller = _$controller_;
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
});