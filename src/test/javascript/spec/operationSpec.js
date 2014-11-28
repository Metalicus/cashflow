'use strict';

describe('Operation Tests ', function () {

    beforeEach(module('cashFlow'));

    describe('OperationEditCtrl', function () {
        var $controller, Operation, Category, Account, Currency, modalInstance;

        beforeEach(inject(function (_$rootScope_, _$controller_, _Operation_, _Category_, _Account_, _Currency_) {
            $controller = _$controller_;
            Category = _Category_;
            Operation = _Operation_;
            Account = _Account_;
            Currency = _Category_;

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };
        }));

        it('should create new Operation if there is no id injected', function () {
            var $scope = {};
            var id = null;
            $controller('OperationEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                Operation: Operation,
                Account: Account,
                Currency: Currency,
                Category: Category,
                id: id
            });

            expect($scope.model.id).toEqual(id);
            expect($scope.model.date).not.toBe(null);
            expect($scope.model.type).toEqual('EXPENSE');
        });

        it('should return false if this is not TRANSFER operation', function () {
            var $scope = {};
            $controller('OperationEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                Operation: Operation,
                Account: Account,
                Currency: Currency,
                Category: Category,
                id: null
            });

            expect($scope.model.type).toEqual('EXPENSE');
            expect($scope.isTransfer()).toBe(false);

            $scope.model.type = 'TRANSFER';
            expect($scope.isTransfer()).toBe(true);
        });
    });
});