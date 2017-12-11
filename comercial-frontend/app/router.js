import EmberRouter from '@ember/routing/router';
import config from './config/environment';

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('login');
  this.route('home', function() {
    this.route('realtime');
    this.route('ranking');
    this.route('restaurante');
  });
});

export default Router;
