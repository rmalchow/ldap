var gulp = require('gulp');
var clean = require('gulp-clean');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var watch = require('gulp-watch');

var target="./dist/"

gulp.task('build-js', function() {
    return gulp.src(
    		[
    			'js/main.js',
    			'js/**/*.js'
    		]
    	)
        .pipe(concat('js/angular-plugin.js'))
        .pipe(gulp.dest(target));
});

gulp.task('default', ['build-js']);

