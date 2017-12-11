import Route from '@ember/routing/route';
import moment from 'moment';
import Ember from 'ember';

export default Route.extend({

  model(params) {

    return Ember.$.ajax('http://localhost:3003/v1/restaurante/' + params.id,{
      success: (aux) => {

        let sobra = calculaSobra(aux.eventos);
        aux.tempo_online += sobra.ONLINE;
        aux.tempo_offline += sobra.OFFLINE;

        aux.tempo_online_fmt =  moment().startOf('day').seconds(aux.tempo_online).format('H:mm:ss');
        aux.tempo_offline_fmt = moment().startOf('day').seconds(aux.tempo_offline).format('H:mm:ss');
        aux.statusAtual = false;

        aux.nome = "Restaurante " + aux.restaurante_id;
        aux.eventos = aux.eventos.map(e => {
          return {
            name: translate(e.name),
            date: moment(e.timestamp).format('DD/MM/YYYY HH:mm:ss')
          }
        });
        let sla = (aux.tempo_online * 100 / (aux.tempo_online + aux.tempo_offline));
        if( isNaN(sla) ) {
          aux.sla = "--";
        } else {
          aux.sla = Math.round( sla * 100 + Number.EPSILON ) / 100; /// (O.o) https://stackoverflow.com/questions/11832914/round-to-at-most-2-decimal-places-only-if-necessary
        }

        aux.chartConfig =  {
          type: 'doughnut',
          data: {
            datasets: [{
              data: [
                aux.tempo_offline,
                aux.tempo_online
              ],
              backgroundColor: [
                '#B41D22',
                '#008600',
              ],
              label: 'Online X Offline'
            }],
            labels: [
              "Tempo Offline",
              "Tempo Online"
            ]
          },
          options: {
            responsive: true,
            legend: {
              position: 'bottom',
            },
            title: {
              display: false,
            },
            animation: {
              animateScale: true,
              animateRotate: true
            }
          }
        };

        return aux;
      }
    });

  }

});

function translate(eventName){
  let obj = {
    'ONLINE' : 'Online',
    'OFFLINE' : 'Offline',
    'AVAILABLE' : 'Disponível',
    'UNAVAILABLE' : 'Indisponível'
  };

  return obj[eventName];
}

function calculaSobra(eventos){
  let ret = {
    'ONLINE':0,
    'OFFLINE':0
  };

  let evtTime = eventos[eventos.length - 1].timestamp,
      now = new Date().getTime();

  ret[eventos[eventos.length - 1].name] = (now - evtTime) / 1000;

  return ret;
}
