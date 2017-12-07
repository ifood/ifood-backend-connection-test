import EmberRouter from '@ember/routing/router';
import config from './config/environment';

const Router = EmberRouter.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('login');
  this.route('home', function() {
    this.route('pedidos');
    this.route('agenda');
    this.route('config', { path: 'config' }, function() {
      this.route('funcionamento', { path: 'funcionamento' });
      this.route('feriados');
      this.route('indisponibilidade');
    });
  });
});

export default Router;
