import Route from '@ember/routing/route';

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
      clientId: this.session.clientId,
      loggedIn: this.session.timestamp
    };

    return this.modelObj;

  },

  actions: {

    logout(){
      clearInterval(this.timer);
      sessionStorage.clear();
      this.transitionTo('/');
    }

  }



});
