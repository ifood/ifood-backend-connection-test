import Route from '@ember/routing/route';
import {inject as service} from '@ember/service';
import moment from 'moment';

export default Route.extend({

  beforeModel() {
    this.session = JSON.parse(sessionStorage.getItem('ifood_test'));
  },

  availability: service('availability'),

  modelObj: {},

  model() {
    this.modelObj = {
      status: this.get('availability').status(),
      reason: -1,
      noReasonError: false,
      dtInicio: null,
      dtFim: null
    };

    return this.modelObj;
  },

  actions: {

    clearError() {
      Ember.set(this.modelObj, 'noReasonError', false);
    },

    unavailable() {

      if (this.modelObj.reason === -1) {
        Ember.set(this.modelObj, 'noReasonError', true);
        return;
      }

      if (confirm("Tem certeza?")) {
        let data = {
          clientId: this.session.clientId,
          timestamp: new Date().getTime(),
          status: 'UN',
          reason: this.modelObj.reason
        };

        Ember.$.ajax('http://localhost:3001/v1/status/unavailable',
          {
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: () => {
              //this.get('availability').setUnavailable();
            },
            error: (err) => {
              console.error(err);
            }
          }
        );
      }

    },

    available() {

      if (confirm("Tem certeza?")) {
        let data = {
          clientId: this.session.clientId,
          timestamp: new Date().getTime(),
          status: 'UN',
          reason: this.modelObj.reason
        };

        Ember.$.ajax('http://localhost:3001/v1/status/available',
          {
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: () => {
              //this.get('availability').setUnavailable();
            },
            error: (err) => {
              console.error(err);
            }
          }
        );
      }

    },

  }

});
