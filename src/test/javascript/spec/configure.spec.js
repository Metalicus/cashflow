'use strict';

describe('Report tests', function () {

    beforeEach(module('cashFlow'));
    beforeEach(module('translateNoop'));

    describe('ConfigureCtrl', function () {
        var $scope, $controller, $translate;

        beforeEach(inject(function (_$rootScope_, _$controller_) {
            $scope = _$rootScope_.$new();
            $controller = _$controller_;
            $translate = {};
        }));

        it('should create default parameters', function () {
            $translate["use"] = function () {
                return null;
            };
            $controller('ConfigureCtrl', {
                $scope: $scope,
                $translate: $translate
            });

            expect($scope.languages).not.toBeUndefined();
            expect($scope.languages.length).toEqual(2);
            expect($scope.languages[0].name).toEqual('English');
            expect($scope.languages[0].value).toEqual('en');
            expect($scope.languages[1].name).toEqual('Russian');
            expect($scope.languages[1].value).toEqual('ru');
            expect($scope.currentLang).toEqual($scope.languages[0]);
        });

        it('should select en language', function () {
            $translate["use"] = function () {
                return 'en';
            };
            $controller('ConfigureCtrl', {
                $scope: $scope,
                $translate: $translate
            });

            expect($scope.currentLang).toEqual($scope.languages[0]);
        });

        it('should select ru language', function () {
            $translate["use"] = function () {
                return 'ru';
            };
            $controller('ConfigureCtrl', {
                $scope: $scope,
                $translate: $translate
            });

            expect($scope.currentLang).toEqual($scope.languages[1]);
        });

        it('should select default language', function () {
            $translate["use"] = function () {
                return 'de';
            };
            $controller('ConfigureCtrl', {
                $scope: $scope,
                $translate: $translate
            });

            expect($scope.currentLang).toEqual($scope.languages[0]);
        });

        it('should react on language change', function () {
            $translate["use"] = function () {
                return 'en';
            };
            $controller('ConfigureCtrl', {
                $scope: $scope,
                $translate: $translate
            });

            spyOn($translate, 'use');

            expect($scope.currentLang).toEqual($scope.languages[0]);
            $scope.currentLang = $scope.languages[1];
            $scope.$digest();
            expect($translate.use).toHaveBeenCalledWith('ru');
        });
    });

});