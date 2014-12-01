'use strict';

describe('Account tests ', function () {

    beforeEach(module('cashFlow'));

    describe('AccountCtrl', function () {
        var $scope, $modal, modalInstance, controller, deferred;

        beforeEach(inject(function (_$controller_, _$q_) {
            $scope = {};

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

            controller = _$controller_('AccountCtrl', {
                $scope: $scope,
                $modal: $modal
            });
        }));

        it('should create default parameters', function () {
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

    describe('AccountEditCtrl: new', function () {
        var $scope, controller, modalInstance, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_) {
            $scope = {};
            $httpBackend = _$httpBackend_;

            // loading of all currencies for select
            $httpBackend
                .expectGET('action/currency').respond('{"content":[] }');

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('AccountEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: null
            });
        }));

        it('should create new Account with default values', function () {
            // we don't expect new requests
            $httpBackend.flush();

            // only known values
            expect($scope.model.id).toEqual(null);

            // rest is undefined
            expect($scope.model.name).toBeUndefined();
            expect($scope.model.currency).toBeUndefined();
            expect($scope.model.balance).toBeUndefined();
        });

        it('pressed save button', function () {
            // we don't expect new requests
            $httpBackend.flush();

            // we expect only insert request
            $httpBackend.expectPOST('action/account').respond({id: 1});

            $scope.submit();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('AccountEditCtrl: edit', function () {
        var $scope, controller, modalInstance, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_) {
            $scope = {};
            $httpBackend = _$httpBackend_;

            // loading of all currencies for select
            $httpBackend
                .expectGET('action/currency').respond('{"content":[] }');

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('AccountEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: 1
            });
        }));

        it('should create new Account with default values', function () {
            // only known values
            expect($scope.model.id).toEqual(1);

            // rest is undefined
            expect($scope.model.name).toBeUndefined();
            expect($scope.model.currency).toBeUndefined();
            expect($scope.model.balance).toBeUndefined();
        });

        it('should load Operation then id specified', function () {
            $httpBackend.expectGET('action/account/1').respond({
                id: 1,
                name: 'account',
                currency: {
                    id: 1,
                    name: 'EUR'
                },
                balance: 10.00
            });
            $httpBackend.flush();

            // load data from server
            expect($scope.model.id).toEqual(1);
            expect($scope.model.name).toBe('account');
            expect($scope.model.balance).toBe(10.00);
            expect($scope.model.currency).toEqual({
                id: 1,
                name: 'EUR'
            });
        });

        it('pressed cancel button', function () {
            // we expect only this
            $httpBackend.expectGET('action/account/1').respond({
                id: 1,
                name: 'account',
                currency: {
                    id: 1,
                    name: 'EUR'
                },
                balance: 10.00
            });
            $httpBackend.flush();

            $scope.cancel();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('cancel');
        });

        it('pressed save button', function () {
            // we expect get and save requests
            $httpBackend.expectGET('action/account/1').respond({
                id: 1,
                name: 'account',
                currency: {
                    id: 1,
                    name: 'EUR'
                },
                balance: 10.00
            });
            $httpBackend.expectPUT('action/account/1').respond({id: 1});

            $scope.submit();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('AccountDeleteCtrl', function () {
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

            controller = _$controller_('AccountDeleteCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: 1
            });
        }));

        it('pressed cancel', function () {
            expect($scope.entityName).toEqual('account');
            $scope.cancel();
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.verifyNoOutstandingRequest();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('cancel');
        });

        it('pressed ok', function () {
            $httpBackend.expectDELETE('action/account/1').respond();
            expect($scope.entityName).toEqual('account');
            $scope.ok();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });
});