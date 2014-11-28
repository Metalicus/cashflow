'use strict';

module.exports = function (grunt) {
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.initConfig({
        karma: {
            unit: {
                configFile: 'src/test/javascript/karma.conf.js',
                singleRun: true
            }
        }
    });

    grunt.registerTask('default', [
        'test'
    ]);
};