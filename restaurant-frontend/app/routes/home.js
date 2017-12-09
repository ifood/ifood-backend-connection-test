import Route from '@ember/routing/route';
import Ember from 'ember';

Ember.LinkComponent.reopen({
  attributeBindings: ['data-badge']
});

export default Route.extend({

  session: {},
  timer: undefined,

  beforeModel(){
    this.session = JSON.parse(sessionStorage.getItem('ifood_test'));
  },

  modelObj: {},

  model(){

    this.modelObj = {
      username: this.session.username,
      clientId: this.session.clientId
    };

    let keepAlive = () => {
      let data = JSON.stringify({
        clientId:  "" + this.session.clientId,
        timestamp: new Date().getTime()
      });

      let success = () => {
        Ember.set(this.modelObj, 'statusconn', true);
      };

      let error = () => {
        Ember.set(this.modelObj, 'statusconn', false);
      };

      Ember.$.ajax('http://localhost:3000/v1/keepalive/ping',
        {
          method: 'POST',
          contentType: 'application/json',
          data: data,
          success: success,
          error: error
        });
    };

    keepAlive();
    this.timer = setInterval(keepAlive, 1000 * 10);

    return this.modelObj;

  },

  actions: {

    logout(){
      clearInterval(this.timer);
      this.transitionTo('/');
    }

  }



});
