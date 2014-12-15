'use strict';

describe('Category tests ', function () {

    beforeEach(module('cashFlow'));
    beforeEach(module('translateNoop'));

    describe('CategoryCtrl', function () {
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

            controller = _$controller_('CategoryCtrl', {
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

    describe('CategoryEditCtrl: new', function () {
        var $scope, controller, modalInstance, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_) {
            $scope = {};
            $httpBackend = _$httpBackend_;

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('CategoryEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: null
            });
        }));

        it('should create new Category with default values', function () {
            // only known values
            expect($scope.model.id).toEqual(null);

            // rest is undefined
            expect($scope.model.name).toBeUndefined();
        });

        it('pressed save button', function () {
            // we expect only insert request
            $httpBackend.expectPOST('action/category').respond({id: 1});

            $scope.submit();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('CategoryEditCtrl: edit', function () {
        var $scope, controller, modalInstance, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_) {
            $scope = {};
            $httpBackend = _$httpBackend_;

            modalInstance = {
                close: jasmine.createSpy('modalInstance.close'),
                dismiss: jasmine.createSpy('modalInstance.dismiss'),
                result: {
                    then: jasmine.createSpy('modalInstance.result.then')
                }
            };

            controller = _$controller_('CategoryEditCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: 1
            });
        }));

        it('should create new Category with default values', function () {
            // only known values
            expect($scope.model.id).toEqual(1);

            // rest is undefined
            expect($scope.model.name).toBeUndefined();
        });

        it('should load Operation then id specified', function () {
            $httpBackend
                .expectGET('action/category/1')
                .respond({
                    id: 1,
                    name: 'cat1'
                });
            $httpBackend.flush();

            // load data from server
            expect($scope.model.id).toEqual(1);
            expect($scope.model.name).toBe('cat1');
        });

        it('pressed cancel button', function () {
            // we expect only this
            $httpBackend
                .expectGET('action/category/1')
                .respond({
                    id: 1,
                    name: 'cat1'
                });
            $httpBackend.flush();

            $scope.cancel();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('cancel');
        });

        it('pressed save button', function () {
            // we expect get and save requests
            $httpBackend.expectGET('action/category/1').respond({
                id: 1,
                name: 'cat1'
            });
            $httpBackend.expectPUT('action/category/1').respond({id: 1});

            $scope.submit();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });

    describe('CategoryDeleteCtrl', function () {
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

            controller = _$controller_('CategoryDeleteCtrl', {
                $scope: $scope,
                $modalInstance: modalInstance,
                id: 1
            });
        }));

        it('pressed cancel', function () {
            expect($scope.entityName).toEqual('category');
            $scope.cancel();
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.verifyNoOutstandingRequest();
            expect(modalInstance.dismiss).toHaveBeenCalledWith('cancel');
        });

        it('pressed ok', function () {
            $httpBackend.expectDELETE('action/category/1').respond();
            expect($scope.entityName).toEqual('category');
            $scope.ok();
            $httpBackend.flush();
            expect(modalInstance.close).toHaveBeenCalled();
        });
    });
});