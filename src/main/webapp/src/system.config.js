/**
 * System configuration for Angular 2 samples
 * Adjust as necessary for your application needs.
 */
(function (global) {
  var paths = {
    'npm:': 'lib/'
  };

  // map tells the System loader where to look for things
  var map = {
    'app': '/', // 'dist',
    '@angular/core': 'npm:@angular/core/bundles/core.umd.js',
    '@angular/common': 'npm:@angular/common/bundles/common.umd.js',
    '@angular/compiler': 'npm:@angular/compiler/bundles/compiler.umd.js',
    '@angular/platform-browser': 'npm:@angular/platform-browser/bundles/platform-browser.umd.js',
    '@angular/platform-browser-dynamic': 'npm:@angular/platform-browser-dynamic/bundles/platform-browser-dynamic.umd.js',
    '@angular/http': 'npm:@angular/http/bundles/http.umd.js',
    '@angular/router': 'npm:@angular/router/bundles/router.umd.js',
    '@angular/forms': 'npm:@angular/forms/bundles/forms.umd.js',

    'rxjs': 'npm:rxjs',
    'angular-in-memory-web-api': 'npm:angular-in-memory-web-api',
    'ng2-webstorage': 'npm:ng2-webstorage',
    'angular2-uuid': 'npm:angular2-uuid',
    'angular2-bootstrap-confirm': 'npm:angular2-bootstrap-confirm/angular2-bootstrap-confirm.js',
    'angular2-bootstrap-confirm/position': 'npm:angular2-bootstrap-confirm/position/index.js'
  };

  // packages tells the System loader how to load when no filename and/or no extension
  var packages = {
    'app': {main: 'main.js', defaultExtension: 'js'},
    'rxjs': {main: 'bundles/RX.min.js', defaultExtension: 'js'},
    'angular2-in-memory-web-api': {main: 'index.js', defaultExtension: 'js'},
    'ng2-webstorage': {main: 'bundles/core.umd.js', defaultExtension: 'js'},
    'angular2-uuid': {main: 'index.js', defaultExtension: 'js'}
  };

  var config = {
    paths: paths,
    map: map,
    packages: packages
  };
  System.config(config);
})(this);
