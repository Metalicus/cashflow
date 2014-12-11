(function () {
    var calculator = angular.module('cashflow-calculator', ['ngTouch']);

    calculator.constant('OPERATION', {
        DIVIDE: 'DIVIDE',
        MULTIPLY: 'MULTIPLY',
        ADD: 'ADD',
        SUBTRACT: 'SUBTRACT'
    });

    calculator.controller('CalculatorModalCtrl', ['$scope', function ($scope) {
        $scope.show = false;
        $scope.toggleCalculator = function () {
            $scope.show = !$scope.show;
        }
    }]);

    calculator.controller('CalculatorCtrl', ['$scope', 'OPERATION', function ($scope, OPERATION) {
        $scope.OPERATION = OPERATION;

        $scope.model = {
            total: null,
            firstNumber: '',
            secondNumber: '',
            newNumber: false,
            operation: null,
            toScreen: function () {
                var screen = '';
                if (this.firstNumber === '')
                    return screen;

                screen += this.firstNumber;

                if (this.operation === null)
                    return screen;

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

                if (this.secondNumber === '')
                    return screen;

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
                this.newNumber = true;
            },
            backspace: function () {
                if (this.secondNumber !== '') {
                    this.secondNumber = this.removeLastNumber(this.secondNumber);
                    return;
                }

                if (this.operation !== null) {
                    this.operation = null;
                    return;
                }

                if (this.firstNumber !== '')
                    this.firstNumber = this.removeLastNumber(this.firstNumber);
            },
            addNumber: function (number) {
                if (this.newNumber) {
                    this.clear();
                    this.newNumber = false;
                }

                if (this.operation === null) {
                    this.firstNumber += number;
                } else {
                    this.secondNumber += number;
                }
            },
            addDelimeter: function () {
                if (this.operation === null) {
                    if (this.firstNumber.indexOf('.') === -1)
                        this.addNumber(this.firstNumber === '' ? '0.' : '.');
                } else {
                    if (this.secondNumber.indexOf('.') === -1)
                        this.addNumber(this.secondNumber === '' ? '0.' : '.');
                }
            },
            setOperation: function (operation) {
                if (this.operation !== null)
                    this.calculate();

                if (this.operation === null) {
                    this.newNumber = false;
                    this.operation = operation;
                }
            },
            removeLastNumber: function (number) {
                number = number.substr(0, number.length - 1);
                if (number.length === 0)
                    return '';
                if (number.charAt(number.length - 1) === '.')
                    return this.removeLastNumber(number);

                return number;
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