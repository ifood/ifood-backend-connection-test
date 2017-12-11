const
  gulp = require('gulp'),
  nodemon = require('gulp-nodemon'),
  livereload = require('gulp-livereload');

gulp.task('develop', () => {
  livereload.listen();
  nodemon({
    script: 'bin/www'
  }).on('restart', () => {
    setTimeout(() => {
      livereload.changed();
    }, 500);
  });
});

gulp.task('default', [
  'develop'
]);
