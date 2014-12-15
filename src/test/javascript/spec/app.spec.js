'use strict';

describe('App tests ', function () {

    beforeEach(module('cashFlow'));
    beforeEach(module('translateNoop'));

    describe('ErrorHandlerInterceptor Test', function () {
        var toaster, $httpBackend, Operation;

        beforeEach(inject(function (_$httpBackend_, _Operation_, _toaster_) {
            $httpBackend = _$httpBackend_;
            Operation = _Operation_;
            toaster = _toaster_;

            spyOn(console, 'log');
            spyOn(toaster, 'pop');
        }));

        it('should call toaster on $http error, no message specified', function () {
            $httpBackend.whenGET('action/operation').respond(500);
            Operation.query();
            $httpBackend.flush();
            expect(toaster.pop).toHaveBeenCalledWith('error', 'error.title', '');
            expect(console.log).not.toHaveBeenCalled();
        });

        it('should call toaster on $http error, with message and stack', function () {
            $httpBackend.whenGET('action/operation/12').respond(500, {
                "message": "error message",
                "stack": "stack trace"
            });
            Operation.get({id: 12});
            $httpBackend.flush();
            expect(toaster.pop).toHaveBeenCalledWith('error', 'error.title', 'error message');
            expect(console.log).toHaveBeenCalledWith('stack trace');
        });

    });

    describe('directove: ServerData', function () {
        var $scope, $compile, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$compile_, _$httpBackend_, _Operation_) {
            $httpBackend = _$httpBackend_;
            $compile = _$compile_;
            $scope = _$rootScope_.$new();

            $scope.gridOptions = {};
            $scope.gridOptions.serverData = {
                source: _Operation_
            };
        }));

        it('should throw exception if there is no source specified', function () {
            // check if there is no source
            delete $scope.gridOptions["serverData"]["source"];
            function errorWrapper() {
                $compile('<table server-data></table>')($scope);
                $scope.$digest();
            }

            expect(errorWrapper).toThrow();

            // check if there is no serverData
            delete $scope.gridOptions["serverData"];
            expect(errorWrapper).toThrow();
        });

        it('should fetch data from server', function () {
            // first default request
            $httpBackend.expectGET('action/operation').respond('{"content":[], "last": true }');

            // init required envirement
            $scope.gridOptions.columnDefs = [];

            $compile('<table server-data></table>')($scope);

            $httpBackend.flush();

            expect($scope.gridOptions.serverData.page).toBeUndefined();
            expect($scope.gridOptions.serverData.last).toBeUndefined();
            expect($scope.gridOptions.serverData.filters).toBeUndefined();
            expect($scope.gridOptions.defaultSort).toBeUndefined();
            expect($scope.gridOptions.getRequestParameters).toBeUndefined();
            expect($scope.gridOptions.fetchData).toBeUndefined();
            expect($scope.gridOptions.resort).toBeUndefined();
        });

    });

    describe('directove: Pageable', function () {
        var $scope, $compile, $httpBackend;

        beforeEach(inject(function (_$rootScope_, _$compile_, _$httpBackend_, _Operation_) {
            $httpBackend = _$httpBackend_;
            $compile = _$compile_;
            $scope = _$rootScope_.$new();

            $scope.gridOptions = {};
            $scope.gridApi = {
                core: {
                    on: {
                        sortChanged: function () {
                        }
                    }
                },
                grid: {
                    getColumnSorting: function () {
                        return [];
                    }
                },
                infiniteScroll: {
                    dataLoaded: function () {
                    },
                    on: {
                        needLoadMoreData: function () {
                        }
                    }
                }
            };

            $scope.gridOptions.serverData = {
                source: _Operation_,
                last: false
            };
        }));

        it('should throw exception if there is no source specified', function () {
            // check if there is no source
            delete $scope.gridOptions["source"];
            function errorWrapper() {
                $compile('<table pageable></table>')($scope);
                $scope.$digest();
            }

            expect(errorWrapper).toThrow();

            // check if there is no serverData
            delete $scope.gridOptions["serverData"];
            expect(errorWrapper).toThrow();
        });

        it('should add all neccessery parameters', function () {
            // first default request
            $httpBackend.expectGET('action/operation?page=0&size=100&sort=id,desc').respond('{"content":[], "last": true }');

            // init required envirement
            $scope.gridOptions.columnDefs = [];

            $compile('<table pageable></table>')($scope);

            $httpBackend.flush();

            expect($scope.gridOptions.serverData.page).toEqual(1);
            expect($scope.gridOptions.serverData.last).toEqual(true);
            // because there is no filters
            expect($scope.gridOptions.serverData.filters).toBeUndefined();

            // and default sorting
            expect($scope.gridOptions.defaultSort.name).toEqual('id');
            expect($scope.gridOptions.defaultSort.dir).toEqual('desc');
        });

        it('should sort only one column', function () {
            // default sorting
            $scope.gridOptions.columnDefs = [
                {name: 'Info', field: 'info', defaultSort: {direction: 'asc'}},
                {name: 'Amount', field: 'amount', defaultSort: {direction: 'desc'}}
            ];

            //request with default sorted column
            $httpBackend.expectGET('action/operation?page=0&size=100&sort=info,asc').respond('{"content":[], "last": true }');
            $compile('<table pageable></table>')($scope);
            $httpBackend.flush();
        });

        it('should sort and filter', function () {
            // default sorting
            $scope.gridOptions.columnDefs = [
                {name: 'Info', field: 'info'},
                {name: 'Amount', field: 'amount', defaultSort: {direction: 'desc'}}
            ];

            //request with default sorted column
            $httpBackend.expectGET('action/operation?page=0&size=100&sort=amount,desc').respond('{"content":[], "last": true }');
            $compile('<table pageable></table>')($scope);
            $httpBackend.flush();

            // let's change sorting
            $httpBackend.expectGET('action/operation?page=0&size=100&sort=info,asc').respond('{"content":[], "last": true }');
            $scope.gridApi.grid.getColumnSorting = function () {
                return [{field: 'info', sort: {direction: 'asc'}}]
            };
            $scope.gridOptions.serverData.resort();
            $httpBackend.flush();
            expect($scope.gridOptions.serverData.page).toEqual(1);

            // check page count increment
            $httpBackend.expectGET('action/operation?page=1&size=100&sort=info,asc').respond('{"content":[], "last": true }');
            $scope.gridOptions.serverData.last = false;
            $scope.gridOptions.serverData.fetchData();
            $httpBackend.flush();
            expect($scope.gridOptions.serverData.page).toEqual(2);

            // check if last page then no fetching
            $scope.gridOptions.serverData.last = true;
            $scope.gridOptions.serverData.fetchData();
            expect($scope.gridOptions.serverData.page).toEqual(2);

            //change filter. this will change 'page' to 0 and 'last' to false
            $httpBackend.expectGET('action/operation?category=10&page=0&size=100&sort=info,asc').respond('{"content":[], "last": true }');
            $scope.filter["category"] = {id: 10};
            $httpBackend.flush();
            expect($scope.gridOptions.serverData.page).toEqual(1);

            // add another filter
            $httpBackend.expectGET('action/operation?category=10&info=test&page=0&size=100&sort=info,asc').respond('{"content":[], "last": true }');
            $scope.filter["info"] = "test";
            $httpBackend.flush();
            expect($scope.gridOptions.serverData.page).toEqual(1);
        });
    });

});