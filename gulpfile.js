var gulp = require('gulp');

var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var jshint = require('gulp-jshint');
var ngmin = require('gulp-ngmin');
var rename = require('gulp-rename');
var notify = require('gulp-notify');

var prefix = 'src/main/webapp/';
var paths = {
    appscripts: [
        prefix + 'js/app.js',
        prefix + 'analytics/*.js',
        prefix + 'charts/*.js',
        prefix + 'global/*.js',
        prefix + 'gettingstarted/*.js'],
    angularscripts: [],
    foundationscripts: [
        prefix + 'foundation-4.3.1/js/foundation/foundation.js',
        prefix + 'foundation-4.3.1/js/foundation/foundation.abide.js',
        prefix + 'foundation-4.3.1/js/foundation/foundation.reveal.js',
        prefix + 'foundation-4.3.1/js/foundation/foundation.orbit.js',
        prefix + 'foundation-4.3.1/js/foundation/foundation.tooltips.js'],
    images: 'client/img/**/*'
};

gulp.task('foundationscripts', function() {
    return gulp.src(paths.foundationscripts)
        .pipe(concat('foundation-all.js'))
        .pipe(gulp.dest(prefix + 'js/libs'))
        .pipe(rename({suffix: '.min'}))
        .pipe(uglify({mangle: false}))
        .pipe(gulp.dest(prefix + 'js/libs'))
//    .pipe(livereload(server))
        .pipe(notify({ message: 'Scripts task complete' }));
});

gulp.task('appscripts', function() {
    return gulp.src(paths.appscripts)
//    .pipe(jshint('.jshintrc'))
        .pipe(jshint.reporter('default'))
        .pipe(concat('index.js'))
        .pipe(ngmin())
        .pipe(gulp.dest(prefix))
        .pipe(rename({suffix: '.min'}))
        .pipe(uglify({mangle: false}))
        .pipe(gulp.dest(prefix))
//    .pipe(livereload(server))
        .pipe(notify({ message: 'Scripts task complete' }));
});

// Rerun the task when a file changes
gulp.task('watch', function() {
    gulp.watch(paths.appscripts, ['appscripts']);
//  gulp.watch(paths.images, ['images']);
});

// The default task (called when you run `gulp` from cli)
gulp.task('default', ['appscripts', 'foundationscripts']);
gulp.task('watch', ['appscripts', 'watch']);