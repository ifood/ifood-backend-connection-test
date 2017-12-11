import Route from '@ember/routing/route';
import moment from 'moment';

export default Route.extend({

  model(params) {

    let aux = {
      "restaurante_id" : "105",
      "data" : moment("2017-12-11T03:00:00.274Z").toISOString(),
      "tempo_online" : 2784,
      "tempo_offline" : 2916,
      "eventos" : [
          {
            "name" : "OFFLINE",
            "timestamp" : 1512961200274
          }
        ]
      };

    aux.statusAtual = false;

    let eventos = [
        {
          "name": "ONLINE",
          "timestamp": 1512962387825
        },
        {
          "name": "OFFLINE",
          "timestamp": 1512962402893
        },
        {
          "name": "ONLINE",
          "timestamp": 1512962435866
        },
        {
          "name": "AVAILABLE",
          "timestamp": 1512962442311
        },
        {
          "name": "UNAVAILABLE",
          "timestamp": 1512962452192
        },
        {
          "name": "AVAILABLE",
          "timestamp": 1512962458598
        },
        {
          "name": "OFFLINE",
          "timestamp": 1512962470771
        }
      ];

    aux.nome = "Restaurante " + aux.restaurante_id;
    aux.eventos = eventos.map(e => {
        return {
          name: translate(e.name),
          date: moment(e.timestamp).format('DD/MM/YYYY HH:mm:ss')
        }
      });
    let sla = (aux.tempo_online * 100 / (aux.tempo_online + aux.tempo_offline));

    aux.sla = Math.round( sla * 100 + Number.EPSILON ) / 100; /// (O.o) https://stackoverflow.com/questions/11832914/round-to-at-most-2-decimal-places-only-if-necessary

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

function translate(eventName){
  let obj = {
    'ONLINE' : 'Online',
    'OFFLINE' : 'Offline',
    'AVAILABLE' : 'Disponível',
    'UNAVAILABLE' : 'Indisponível'
  };

  return obj[eventName];
}
