import Route from '@ember/routing/route';

export default Route.extend({

  model(){
    return {
      topRanked: [
        {id: 1, nome: 'Restaurante 1', sla: 97 },
        {id: 10, nome: 'Restaurante 10', sla: 96.8 },
        {id: 23, nome: 'Restaurante 23', sla: 96.2 },
      ],
      bottomRanked: [
        {id: 10, nome: 'Restaurante 10', sla: 23 },
        {id: 100, nome: 'Restaurante 100', sla: 25.9 },
        {id: 230, nome: 'Restaurante 230', sla: 26.7 },
      ]
    }
  }

});
