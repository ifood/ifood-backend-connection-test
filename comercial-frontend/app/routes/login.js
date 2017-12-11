import Route from '@ember/routing/route';

export default Route.extend({

  model() {

    return {
      username: 'fakeauth',
      password: 'anypass'
    }
  },

  actions: {
    auth(model) {
      let session = JSON.stringify({
        username: model.username,
        timestamp: new Date()
      });
      sessionStorage.ifood_test = session;
      this.transitionTo('home.realtime');
    }
  }

});
