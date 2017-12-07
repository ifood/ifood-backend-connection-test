import Route from '@ember/routing/route';

let FIXT = [
  {
    ini: '2017-12-12T12:30:00Z',
    end: '2017-12-12T14:30:00Z',
    reason: 'Connection Issues'
  },
  {
    ini: '2017-12-25T02:00:00Z',
    end: '2017-12-26T02:00:00Z',
    reason: 'Holiday'
  }
];

export default Route.extend({

  modelObj: {},

  model(){

    let reasonList = [
      {id: 1, reason: 'Lack of delivery staff'},
      {id: 2, reason: 'Connection Issues'},
      {id: 3, reason: 'Overloaded Offline'},
      {id: 4, reason: 'Holiday'}
    ];

    this.modelObj = {
      list: FIXT,
      reasons: reasonList,
      dataini: "",
      datafim: "",
      reason: ""
    };

    return this.modelObj;

  },

  actions: {

    selectReason(reason){
      Ember.set(this.modelObj, 'reason', reason);
    },

    newRecord(){
      console.log("pushing...");
      let iniDate = new Date();
      let endDate = new Date(new Date().getTime() + 1000);
      let reason = 'dummie' + Math.random();

      this.modelObj.list.pushObject({
        ini: iniDate,
        end: endDate,
        reason: reason
      });

    }
  }

});
