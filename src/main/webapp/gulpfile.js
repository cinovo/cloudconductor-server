const del = require('del');
const gulp = require('gulp');
const typescript = require('gulp-typescript');
const flatten = require('gulp-flatten');
const sass = require('gulp-sass');
const tsconfig = require('tsconfig-glob');
const sourcemaps = require('gulp-sourcemaps');
const uglify = require('gulp-uglify');
var systemjsBuilder = require('systemjs-builder');
const concat = require('gulp-concat');
const merge = require('merge-stream');
const browserSync = require('browser-sync');
var url = require('url');
var proxy = require('proxy-middleware');

const cleanCSS = require('gulp-clean-css');
const seq = require('run-sequence');
const plumber = require('gulp-plumber');
const colors  = require('colors');

const conf = require('./gulpfile.config.json');
const tsConfig = require(conf.tsconfig);

gulp.task('prep:tsConfig', function () {
  return tsconfig({configPath: ".", indent: 2});
});

// copy dependencies
gulp.task('prep:libs', function () {
  var pipes = [];
  conf.libs.forEach(function(lib){
    pipes.push(gulp.src([lib.src])
      .pipe(gulp.dest(lib.target)));
  });
  pipes.push(gulp.src(conf.vendor.src)
    .pipe(concat('vendors.min.js'))
    .pipe(uglify())
    .pipe(gulp.dest(conf.vendor.target)));

  return merge(pipes);
});

gulp.task('compile:ts', function () {
  var tsProject = typescript.createProject(conf.tsconfig);
  var tsResult = tsProject.src()
    .pipe(sourcemaps.init())
    .pipe(tsProject(typescript.reporter.defaultReporter()));
  return tsResult.js
    .pipe(plumber({
      errorHandler: function (err) {
        console.error('>>> [tsc] Typescript compilation failed'.bold.green);
        this.emit('end');
      }}))
    .pipe(sourcemaps.write('.'))
    //.pipe(flatten({subPath: [1]}))
    .pipe(gulp.dest(conf.target));
});

//bundle js files
gulp.task('bundle:js', function () {
  var sysJSBuilder = new systemjsBuilder(conf.target, 'src/system.config.js');
  return sysJSBuilder.buildStatic('main.js', conf.target + '/main.min.js');
});

gulp.task('clean:bundle', function () {
  return del([conf.target+'/app',conf.target+'/lib', conf.target + '/app.*.js*', conf.target + '/main.js*']);
});

// Minify JS bundle
gulp.task('post:minify:js', function() {
  return gulp
    .src(conf.target + '/main.min.js')
    .pipe(uglify())
    .pipe(gulp.dest(conf.target));
});

// copy static assets - i.e. non TypeScript compiled source
gulp.task('post:copy:assets', function () {

  var pipes = [];
  conf.assets.forEach(function(asset) {
    pipes.push(
      gulp.src(asset.src, {base: './'})
        .pipe(flatten({subPath: asset.flatten}))
        .pipe(gulp.dest(asset.target))
    );
  });

  return pipes;
});

// Compile SCSS to CSS, concatenate, and minify
gulp.task('compile:sass', function () {
  var styles = [];
  conf.styles.forEach(function(style){
    var g = gulp.src(style.src)
      .pipe(sourcemaps.init({largeFile: true}))
      .pipe(plumber({
        errorHandler: function (err) {
          console.error('>>> [sass] Sass style compilation failed'.bold.green);
          console.error(err.message);
          this.emit('end');
        }}))
      .pipe(sass().on('error', sass.logError));
    if(style.file) {
      g.pipe(concat(style.file));
    }else {
      g.pipe(flatten({subPath: [0,0]}));
    }
    g.pipe(cleanCSS())
      .pipe(sourcemaps.write())
      .pipe(gulp.dest(style.target));
    styles.push(g);
  });

  return styles;
});



// Run browsersync for development
gulp.task('serve', function (callback) {
  return seq('build', 'serve:plain', callback);
});

// Run browsersync for development
gulp.task('serve:plain', function () {

  var proxyOptions = url.parse('http://localhost:8080/api');
  proxyOptions.route = '/api';

  browserSync({
    server: {
      baseDir: conf.target,
      middleware: [proxy(proxyOptions)]
    }
  });

  gulp.watch(['src/**/*.html', 'public/**/*'], ['reloadHTML']);
  gulp.watch(['src/**/*.scss'], ['reloadCSS']);
  gulp.watch(['src/**/*.ts'], ['reloadJS']);
});



// clean the contents of the distribution directory
gulp.task('clean', function () {
  return del(conf.target + "/**/*");
});

gulp.task('prep', ['prep:tsConfig']);

gulp.task('minCompileJS', function(callback) {
  seq('prep:tsConfig', 'compile:ts', 'bundle:js', callback);
});

gulp.task('compileJS', function(callback) {
  seq('prep:libs', 'compile:ts', 'bundle:js', callback);
});

gulp.task('compileCSS', function(callback) {
  seq('compile:sass', callback);
});

gulp.task('compileHTML', function(callback) {
  seq('post:copy:assets', callback);
});

gulp.task('build', function(callback) {
  seq('clean', 'prep', 'compileJS', ['compileCSS', 'compileHTML'], callback);
});

gulp.task('release', function(callback) {
  seq('clean', 'prep', 'compileJS', 'post:minify:js', 'clean:bundle', ['compileCSS', 'compileHTML'], callback);
});

gulp.task('reloadJS', ['minCompileJS'], browserSync.reload);
gulp.task('reloadCSS', ['compileCSS'], browserSync.reload);
gulp.task('reloadHTML', ['compileHTML'], browserSync.reload);
