var gulp = require('gulp');
var template_cache = require('gulp-angular-templatecache');
var clean = require('gulp-clean');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var watch = require('gulp-watch');

var target="../resources/public/ui/dist/"
 
gulp.task('build-js', function() {
    return gulp.src(
    		[
    			'main/main.js',
    			'main/js/**/*.js',
    			'modules/*/module.js',
    			'modules/*/js/**/*.js'
    		]
    	)
        .pipe(concat('js/app.js'))
        .pipe(gulp.dest(target));
});

gulp.task("build-templates", function() {
	  return gulp
	    .src(["main/templates/**/*.html", "modules/*/templates/**/*.html"])
	    .pipe(template_cache("**", { standalone: false }))
	    .pipe(concat("js/templates.js"))
	    .pipe(gulp.dest(target));
});


gulp.task('build-css', function(){
    return gulp.src(
	[
        'fontawesome-5/css/all.min.css',
        'node_modules/select2/dist/css/select2.min.css',
        'css/*.css'
    	]
	)
        .pipe(concat('css/styles.css'))
        .pipe(gulp.dest(target));

});

gulp.task('build-fonts', function(){
    return gulp.src(['node_modules/bootstrap/dist/fonts/*'])
        .pipe(rename({
            dirname: 'fonts/'
        }))
        .pipe(gulp.dest(target));
});

gulp.task('build-fontawesome', function(){
	return gulp.src(['fontawesome-5/webfonts/*'])
	.pipe(rename({
		dirname: 'webfonts/'
	}))
	.pipe(gulp.dest(target));
});

gulp.task('build-deps', function() {
	var jsFiles = [
        'node_modules/jquery/dist/jquery.min.js',
        'node_modules/underscore/underscore-min.js',
        'node_modules/angular/angular.min.js',
        'extra/angular-select/angular-select.js',        
        'node_modules/angular-local-storage/dist/angular-local-storage.min.js',
        'node_modules/moment/min/moment-with-locales.min.js',
        'node_modules/moment-timezone/builds/moment-timezone-with-data.min.js',
        'node_modules/angular-moment/angular-moment.min.js',
        'node_modules/angular-route/angular-route.min.js',
        'node_modules/angular-upload/angular-upload.js',
        'extra/angular-plugin/dist/js/angular-plugin.js',
        'extra/bootstrap.min.js',
        'node_modules/chart.js/dist/Chart.min.js',
        'node_modules/angular-chart.js/dist/angular-chart.min.js',
        'node_modules/restangular/dist/restangular.min.js',
        'node_modules/angular-date-picker/angular-date-picker.js',
        'node_modules/select2/dist/js/select2.min.js',
        'node_modules/otpauth/dist/otpauth.min.js'
    ];
    return gulp.src(jsFiles)
        .pipe(concat('js/dependencies.js'))
        .pipe(gulp.dest(target));
});

gulp.task('clean', function () {
    return gulp.src(target, {read: false})
        .pipe(clean());
});

gulp.task('build', gulp.series('build-js', 'build-deps', 'build-fonts', 'build-fontawesome', 'build-templates', 'build-css'));

gulp.task('default', gulp.series('build'));

gulp.task('watch', gulp.series('build'), function () {
    gulp.watch("src/**/*.js", ['build']);
    gulp.watch("src/**/*.html", ['build']);
});
