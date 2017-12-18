/**
 * Service definition for the availability service
 */

import Ember from 'ember'
const {Service} = Ember

export default Service.extend({

  _status: {
    available: undefined,
    lastAvailable: undefined,
    unavailableSince: undefined
  },

  setAvailable(){
    Ember.set(this._status, 'available', true);
    Ember.set(this._status, 'lastAvailable', new Date().toISOString());
  },

  setUnavailable(){
    Ember.set(this._status, 'available', false);
    Ember.set(this._status, 'unavailableSince', new Date().toISOString());
  },

  status(){
    return this._status;
  }

})
