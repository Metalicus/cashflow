(function () {
    var configure = angular.module('cashflow-configure', []);

    configure.controller('ConfigureCtrl', ['$scope', '$translate', function ($scope, $translate) {
        $scope.languages = [
            {name: 'English', value: 'en'},
            {name: 'Russian', value: 'ru'}
        ];
        $scope.currentLang = null;

        var current = $translate.use();
        for (var i = 0; i < $scope.languages.length; i++) {
            if (current === $scope.languages[i].value) {
                $scope.currentLang = $scope.languages[i];
                break;
            }
        }

        if ($scope.currentLang === null)
            $scope.currentLang = $scope.languages[0];

        $scope.$watch('currentLang', function () {
            $translate.use($scope.currentLang.value);
        });
    }]);
})();