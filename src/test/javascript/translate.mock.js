'use strict';

angular.module('translateNoop', [])
    .factory('$translateStaticFilesLoader', function ($q) {
        return function () {
            var deferred = $q.defer();
            deferred.resolve({});
            return deferred.promise;
        };
    });