(function () {
    var calculator = angular.module('cashflow-calculator', ['ngTouch']);

    calculator.constant('OPERATION', {
        DIVIDE: 'DIVIDE',
        MULTIPLY: 'MULTIPLY',
        ADD: 'ADD',
        SUBTRACT: 'SUBTRACT'
    });

    calculator.controller('CalculatorCtrl', ['$scope', 'OPERATION', function ($scope, OPERATION) {
        $scope.OPERATION = OPERATION;

        $scope.model = {
            firstNumber: '',
            secondNumber: '',
            operation: null,
            getNumber: function () {

            },
            toScreen: function () {
                var screen = '';
                if (this.firstNumber !== '')
                    screen += this.firstNumber;
                if (this.operation !== null) {
                    switch (this.operation) {
                        case OPERATION.DIVIDE:
                            screen += '/';
                            break;
                        case OPERATION.MULTIPLY:
                            screen += '*';
                            break;
                        case OPERATION.ADD:
                            screen += '+';
                            break;
                        case OPERATION.SUBTRACT:
                            screen += '-';
                            break;
                    }
                }
                if (this.secondNumber !== '')
                    screen += this.secondNumber;

                return screen;
            },
            clear: function () {
                this.firstNumber = '';
                this.secondNumber = '';
                this.operation = null;
            },
            calculate: function () {
                if (this.firstNumber === '' || this.secondNumber === '' || this.operation === null)
                    return;

                var result;
                switch (this.operation) {
                    case OPERATION.DIVIDE:
                        result = (Number(this.firstNumber) / Number(this.secondNumber)).toString();
                        break;
                    case OPERATION.MULTIPLY:
                        result = (Number(this.firstNumber) * Number(this.secondNumber)).toString();
                        break;
                    case OPERATION.ADD:
                        result = (Number(this.firstNumber) + Number(this.secondNumber)).toString();
                        break;
                    case OPERATION.SUBTRACT:
                        result = (Number(this.firstNumber) - Number(this.secondNumber)).toString();
                        break;
                }

                this.firstNumber = result;
                this.secondNumber = '';
                this.operation = null;
            },
            backspace: function () {
                if (this.secondNumber !== '') {
                    this.secondNumber = this.secondNumber.substr(0, this.secondNumber.length - 1);
                    if (this.secondNumber.length == 0)
                        this.secondNumber = '';

                    return;
                }

                if (this.operation !== null) {
                    this.operation = null;
                    return;
                }

                if (this.firstNumber !== '') {
                    this.firstNumber = this.firstNumber.substr(0, this.firstNumber.length - 1);
                    if (this.firstNumber.length == 0)
                        this.firstNumber = '';
                }
            },
            addNumber: function (number) {
                if (this.operation === null) {
                    this.firstNumber += number;
                } else {
                    this.secondNumber += number;
                }
            },
            addDelimeter: function () {
                var delimeter = '.';
                if (this.operation === null) {
                    if (this.firstNumber === '')
                        delimeter = '0.';

                    if (this.firstNumber.indexOf('.') === -1)
                        this.firstNumber += delimeter;
                } else {
                    if (this.secondNumber === '')
                        delimeter = '0.';

                    if (this.secondNumber.indexOf('.') === -1)
                        this.secondNumber += delimeter;
                }
            },
            setOperation: function (operation) {
                if (this.operation !== null)
                    this.calculate();

                if (this.operation === null)
                    this.operation = operation;
            }
        };

        $scope.clear = function () {
            $scope.model.clear();
        };

        $scope.backspace = function () {
            $scope.model.backspace();
        };

        $scope.calculate = function () {
            $scope.model.calculate();
        };

        $scope.pressDelimeter = function () {
            $scope.model.addDelimeter();
        };

        $scope.pressNum = function (number) {
            $scope.model.addNumber(number);
        };

        $scope.pressOpertaion = function (operation) {
            $scope.model.setOperation(operation);
        };

        $scope.$watch('model', function () {
            $scope.screen = $scope.model.toScreen();
        }, true);
    }]);
})();