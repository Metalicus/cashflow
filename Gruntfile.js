'use strict';

module.exports = function (grunt) {
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    grunt.initConfig({
        cashflow: {
            dist: 'dist'
        },
        bower: {
            install: {
                options: {
                    targetDir: "src/main/webapp/bower_components"
                }
            }
        },
        karma: {
            unit: {
                configFile: 'src/test/javascript/karma.conf.js',
                singleRun: true
            }
        },
        useminPrepare: {
            html: 'src/main/webapp/**/*.html',
            options: {
                dest: '<%= cashflow.dist %>'
            }
        },
        usemin: {
            html: ['<%= cashflow.dist %>/**/*.html'],
            css: ['<%= cashflow.dist %>/css/**/*.css'],
            options: {
                assetsDirs: ['<%= cashflow.dist %>/**/'],
                dirs: ['<%= cashflow.dist %>']
            }
        },
        htmlmin: {
            dist: {
                options: {
                    removeCommentsFromCDATA: true,
                    collapseWhitespace: true,
                    collapseBooleanAttributes: true,
                    conservativeCollapse: true,
                    removeAttributeQuotes: true,
                    removeRedundantAttributes: true,
                    useShortDoctype: true,
                    removeEmptyAttributes: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= cashflow.dist %>',
                    src: ['*.html', 'template/**/*.html'],
                    dest: '<%= cashflow.dist %>'
                }]
            }
        },
        copy: {
            dist: {
                expand: true,
                dot: true,
                cwd: 'src/main/webapp',
                dest: '<%= cashflow.dist %>',
                src: [
                    '*.html',
                    'template/*.html',
                    'WEB-INF/*.xml'
                ]
            },
            bootstrap: {
                expand: true,
                flatten: true,
                cwd: 'src/main/webapp/bower_components/bootstrap/fonts',
                src: [
                    '**/*.*'
                ],
                dest: '<%= cashflow.dist %>/fonts'
            },
            grid: {
                expand: true,
                flatten: true,
                cwd: 'src/main/webapp/bower_components/angular-ui-grid',
                src: [
                    'ui-grid.eot',
                    'ui-grid.svg',
                    'ui-grid.ttf',
                    'ui-grid.woff'
                ],
                dest: '<%= cashflow.dist %>/css'
            }
        },
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        '<%= cashflow.dist %>'
                    ]
                }]
            }
        }
    });

    grunt.registerTask('test', [
        'karma'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'concat',
        'copy:dist',
        'copy:bootstrap',
        'copy:grid',
        'uglify',
        'usemin',
        'cssmin',
        'htmlmin'
    ]);

    grunt.registerTask('dist', [
        'test',
        'build'
    ]);

    grunt.registerTask('default', [
        'test',
        'build'
    ]);
};