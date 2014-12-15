'use strict';

describe('Report tests', function () {

    beforeEach(module('cashFlow'));
    beforeEach(module('translateNoop'));

    describe('ReportListCtrl', function () {
        var $scope, controller, REPORT_TYPE;

        beforeEach(inject(function (_$controller_, _REPORT_TYPE_) {
            $scope = {};
            REPORT_TYPE = _REPORT_TYPE_;

            controller = _$controller_('ReportListCtrl', {
                $scope: $scope
            });
        }));

        it('should create default parameters', function () {
            expect($scope.REPORT_TYPE).not.toBeUndefined();
            expect($scope.current).toEqual(REPORT_TYPE.MONTHLY_BALANCE);
        });

        it('should check if selected report is active', function () {
            expect($scope.isActive(REPORT_TYPE.MONTHLY_BALANCE)).toEqual(true);
            expect($scope.isActive('SOME_OTHER_REPORT')).toEqual(false);

            $scope.current = 'SOME_OTHER_REPORT';
            expect($scope.isActive(REPORT_TYPE.MONTHLY_BALANCE)).toEqual(false);
            expect($scope.isActive('SOME_OTHER_REPORT')).toEqual(true);
        });

        it('should change current report', function () {
            expect($scope.current).toEqual(REPORT_TYPE.MONTHLY_BALANCE);
            $scope.selectReport('SOME_OTHER_REPORT');
            expect($scope.current).toEqual('SOME_OTHER_REPORT');
        });
    });

    describe('MonthlyBalanceCrtl', function () {
        var $scope, $httpBackend, REPORT_TYPE, controller;

        beforeEach(inject(function (_$rootScope_, _$controller_, _$httpBackend_, _REPORT_TYPE_) {
            $scope = _$rootScope_.$new();
            REPORT_TYPE = _REPORT_TYPE_;
            $httpBackend = _$httpBackend_;

            controller = _$controller_('MonthlyBalanceCrtl', {
                $scope: $scope
            });
        }));

        it('should create default scope variables', function () {
            expect($scope.months).not.toBeUndefined();
            expect($scope.months.length).toEqual(12);

            expect($scope.years).not.toBeUndefined();
            expect($scope.years.length).toEqual(8);
            expect($scope.years[0]).toEqual(2013);
            expect($scope.years[1]).toEqual(2014);
            expect($scope.years[2]).toEqual(2015);
            expect($scope.years[3]).toEqual(2016);
            expect($scope.years[4]).toEqual(2017);
            expect($scope.years[5]).toEqual(2018);
            expect($scope.years[6]).toEqual(2019);
            expect($scope.years[7]).toEqual(2020);

            expect($scope.request).not.toBeUndefined();
            expect($scope.request.type).toEqual(REPORT_TYPE.MONTHLY_BALANCE);
            expect($scope.request.month).toEqual($scope.months[new Date().getMonth()]);
            expect($scope.request.year).toEqual(new Date().getFullYear());

            expect($scope.report).not.toBeUndefined();
            expect(Object.keys($scope.report).length).toEqual(0);

            expect($scope.loadReport).not.toBeUndefined();
        });

        it('should load report for current month and year', function () {
            var date = new Date();
            $httpBackend
                .expectGET('action/report/' + REPORT_TYPE.MONTHLY_BALANCE + '?month=' + date.getMonth() + '&year=' + date.getFullYear())
                .respond('{}');

            $httpBackend.flush();
        });

        it('should request new report if month or year changed', function () {
            // now we check default request
            var date = new Date();
            $httpBackend
                .expectGET('action/report/' + REPORT_TYPE.MONTHLY_BALANCE + '?month=' + date.getMonth() + '&year=' + date.getFullYear())
                .respond('{}');
            $httpBackend.flush();

            // now change month for january
            $httpBackend
                .expectGET('action/report/' + REPORT_TYPE.MONTHLY_BALANCE + '?month=0' + '&year=' + date.getFullYear())
                .respond('{}');
            $scope.request.month = $scope.months[0];
            $scope.$digest();
            $httpBackend.flush();

            // change year
            $httpBackend
                .expectGET('action/report/' + REPORT_TYPE.MONTHLY_BALANCE + '?month=0' + '&year=2015')
                .respond('{}');
            $scope.request.year = 2015;
            $scope.$digest();
            $httpBackend.flush();
        });
    });

});