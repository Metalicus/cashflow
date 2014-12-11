'use strict';

describe('Calculator tests', function () {

    beforeEach(module('cashFlow'));

    describe('CalculatorCtrl', function () {
        var $scope, controller, CALC_OPERATION;

        beforeEach(inject(function (_$rootScope_, _$controller_, _CALC_OPERATION_) {
            $scope = _$rootScope_.$new();
            CALC_OPERATION = _CALC_OPERATION_;

            controller = _$controller_('CalculatorCtrl', {
                $scope: $scope
            });
        }));

        it('should create default parameters', function () {
            expect($scope.CALC_OPERATION).not.toBeUndefined();
            expect($scope.CALC_OPERATION.DIVIDE).toEqual('DIVIDE');
            expect($scope.CALC_OPERATION.MULTIPLY).toEqual('MULTIPLY');
            expect($scope.CALC_OPERATION.ADD).toEqual('ADD');
            expect($scope.CALC_OPERATION.SUBTRACT).toEqual('SUBTRACT');

            expect($scope.model).not.toBeUndefined();
            expect($scope.model.firstNumber).toEqual('');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.newNumber).toEqual(false);
            expect($scope.model.operation).toEqual(null);
            expect($scope.screen).toBeUndefined();

            expect($scope.clear).not.toBeUndefined();
            expect($scope.backspace).not.toBeUndefined();
            expect($scope.calculate).not.toBeUndefined();
            expect($scope.pressDecimalMark).not.toBeUndefined();
            expect($scope.pressNum).not.toBeUndefined();
            expect($scope.pressOpertaion).not.toBeUndefined();
        });

        it('should caluclate', function () {
            $scope.model.firstNumber = '1';
            $scope.model.secondNumber = '2';
            $scope.model.operation = CALC_OPERATION.ADD;
            $scope.model.calculate();
            expect($scope.model.firstNumber).toEqual('3');
            $scope.model.secondNumber = '1';
            $scope.model.operation = CALC_OPERATION.SUBTRACT;
            $scope.model.calculate();
            expect($scope.model.firstNumber).toEqual('2');
            $scope.model.secondNumber = '8';
            $scope.model.operation = CALC_OPERATION.MULTIPLY;
            $scope.model.calculate();
            expect($scope.model.firstNumber).toEqual('16');
            $scope.model.secondNumber = '4';
            $scope.model.operation = CALC_OPERATION.DIVIDE;
            $scope.model.calculate();
            expect($scope.model.firstNumber).toEqual('4');
        });

        it('should genereta screen view', function () {
            $scope.pressNum(1);
            expect($scope.model.toScreen()).toEqual('1');
            $scope.pressOpertaion(CALC_OPERATION.ADD);
            expect($scope.model.toScreen()).toEqual('1+');
            $scope.pressNum(2);
            expect($scope.model.toScreen()).toEqual('1+2');
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            expect($scope.model.toScreen()).toEqual('3-');
            $scope.pressNum(1);
            expect($scope.model.toScreen()).toEqual('3-1');
            $scope.pressOpertaion(CALC_OPERATION.MULTIPLY);
            expect($scope.model.toScreen()).toEqual('2*');
            $scope.pressNum(8);
            expect($scope.model.toScreen()).toEqual('2*8');
            $scope.pressOpertaion(CALC_OPERATION.DIVIDE);
            expect($scope.model.toScreen()).toEqual('16/');
            $scope.pressNum(4);
            expect($scope.model.toScreen()).toEqual('16/4');
            $scope.calculate();
            expect($scope.model.toScreen()).toEqual('4');
        });

        it('should genereta screen view with long numbers', function () {
            $scope.pressNum(1);
            expect($scope.model.toScreen()).toEqual('1');
            $scope.pressNum(2);
            expect($scope.model.toScreen()).toEqual('12');
            $scope.pressNum(3);
            expect($scope.model.toScreen()).toEqual('123');
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            $scope.pressNum(1);
            expect($scope.model.toScreen()).toEqual('123-1');
            $scope.pressNum(2);
            expect($scope.model.toScreen()).toEqual('123-12');
            $scope.pressNum(3);
            expect($scope.model.toScreen()).toEqual('123-123');
        });

        it('should calculate only then all variables is set', function () {
            $scope.calculate();
            $scope.$digest();
            expect($scope.screen).not.toBeUndefined();
            expect($scope.screen).toEqual('');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(false);

            $scope.pressNum(1);
            $scope.$digest();
            expect($scope.screen).toEqual('1');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('1');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(false);

            $scope.pressNum(2);
            $scope.$digest();
            expect($scope.screen).toEqual('12');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('12');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(false);

            // no operation and secondNumber, nothing will change
            $scope.calculate();
            $scope.$digest();
            expect($scope.screen).toEqual('12');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('12');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(false);

            // set operation SUBSTRACT
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            $scope.$digest();
            expect($scope.screen).toEqual('12-');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('12');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            expect($scope.model.newNumber).toEqual(false);

            // again, no calculation, because of secondNumber
            $scope.calculate();
            $scope.$digest();
            expect($scope.screen).toEqual('12-');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('12');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            expect($scope.model.newNumber).toEqual(false);

            $scope.pressNum(8);
            $scope.$digest();
            expect($scope.screen).toEqual('12-8');
            expect($scope.model.secondNumber).toEqual('8');
            expect($scope.model.firstNumber).toEqual('12');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            expect($scope.model.newNumber).toEqual(false);

            // and now it will calculate
            $scope.calculate();
            $scope.$digest();
            expect($scope.screen).toEqual('4');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('4');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(true);
        });

        it('should calculate before set second operation', function () {
            $scope.pressNum(1);
            $scope.pressNum(2);
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            $scope.pressNum(8);
            $scope.pressOpertaion(CALC_OPERATION.ADD);
            $scope.$digest();

            expect($scope.screen).toEqual('4+');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('4');
            expect($scope.model.operation).toEqual(CALC_OPERATION.ADD);
            expect($scope.model.newNumber).toEqual(false);
        });

        it('should clear on new number', function () {
            $scope.pressNum(1);
            $scope.pressNum(2);
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            $scope.pressNum(8);
            $scope.calculate();
            $scope.$digest();

            expect($scope.screen).toEqual('4');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('4');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(true);

            $scope.pressNum(5);
            $scope.$digest();
            expect($scope.screen).toEqual('5');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('5');
            expect($scope.model.operation).toEqual(null);
            expect($scope.model.newNumber).toEqual(false);
        });

        it('should set operation once', function () {
            $scope.pressNum(1);
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('1');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.pressOpertaion(CALC_OPERATION.DIVIDE);
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('1');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.pressOpertaion(CALC_OPERATION.ADD);
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('1');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
        });

        it('should remove decimal mark if operation pressed before adding number', function () {
            $scope.pressDecimalMark();
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('0.');
            expect($scope.model.operation).toEqual(null);
            $scope.pressDecimalMark();
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('0.');
            expect($scope.model.operation).toEqual(null);
            $scope.pressOpertaion(CALC_OPERATION.ADD);
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.firstNumber).toEqual('0');
            expect($scope.model.operation).toEqual(CALC_OPERATION.ADD);
            $scope.pressDecimalMark();
            expect($scope.model.firstNumber).toEqual('0');
            expect($scope.model.secondNumber).toEqual('0.');
            $scope.pressNum(1);
            expect($scope.model.firstNumber).toEqual('0');
            expect($scope.model.secondNumber).toEqual('0.1');
        });

        it('should add only one decimal mark to number', function () {
            $scope.pressDecimalMark();
            $scope.pressDecimalMark();
            $scope.pressDecimalMark();
            $scope.pressNum(9);
            $scope.pressOpertaion(CALC_OPERATION.ADD);
            $scope.pressNum(1);
            $scope.pressDecimalMark();
            $scope.pressDecimalMark();
            $scope.pressDecimalMark();
            $scope.pressNum(1);
            expect($scope.model.firstNumber).toEqual('0.9');
            expect($scope.model.secondNumber).toEqual('1.1');
            expect($scope.model.operation).toEqual(CALC_OPERATION.ADD);
        });

        it('backspace delete last number and operation', function () {
            $scope.pressNum(1);
            expect($scope.model.firstNumber).toEqual('1');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.pressDecimalMark();
            expect($scope.model.firstNumber).toEqual('0.');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('0');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.pressNum(3);
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.pressOpertaion(CALC_OPERATION.ADD);
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(CALC_OPERATION.ADD);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.pressOpertaion(CALC_OPERATION.SUBTRACT);
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.pressNum(2);
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('2');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.pressDecimalMark();
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('2.');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.pressNum(9);
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('2.9');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('2');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(CALC_OPERATION.SUBTRACT);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('3');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
            $scope.backspace();
            expect($scope.model.firstNumber).toEqual('');
            expect($scope.model.secondNumber).toEqual('');
            expect($scope.model.operation).toEqual(null);
        });

    });

});