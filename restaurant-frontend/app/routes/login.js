import Route from '@ember/routing/route';
import Ember from 'ember';

export default Route.extend({

  model(){
    return {
      username: '',
      password: 'notimportanthere'
    }
  },

  actions:{
    auth(model){
      if( !model.username ){
        Ember.set(model, 'errorMessage', 'Necessário informar um usuário');
        return false;
      } else {
        let aux = model.username.split("_");
        if( aux.length < 2 ){
          Ember.set(model, 'errorMessage', 'Formato de usuario inválido');
          return false;
        } else {
          if ( aux[1] < 1 || aux[1] > 100000 ){
            Ember.set(model, 'errorMessage', 'O usuário tem que estar entre 1 e 100000');
            return false;
          } else {
            let session = JSON.stringify({
              username: model.username,
              clientId: aux[1]
            });
            sessionStorage.ifood_test = session;
            this.transitionTo('home.pedidos');
          }
        }
      }
    }
  }

});
