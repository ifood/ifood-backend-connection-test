import Route from '@ember/routing/route';
import Ember from 'ember';

export default Route.extend({

  socketIOService: Ember.inject.service('socket-io'),

  beforeModel() {
    const socket = this.get('socketIOService').socketFor('http://localhost:3003/');
    socket.on('updatechart', function (obj) {
      /*
            {
              id: 79,
              state: 'ONLINE | OFFLINE | UNAVAILABLE'
            }
            */
      Ember.$('#ID_' + obj.id).css('fill', colors[obj.state]);

    });

  },

  model() {
    return {
      grupos: ['A', 'B', 'C', 'D', 'E']
    };
  },

  actions: {
    selectGrupo(grupo) {
      console.log("selecionado: ", grupo);

      Ember.$('#container').empty().append('<div id="chart"></div>');

      Ember.$.ajax('http://localhost:3003/v1/dashboard/restaurants?group=' + grupo, {
        method: 'GET',
        contentType: 'application/json',
        success: (data, xhr) => {
          console.log("data", data);
          let objs = data.map(elem => {
            return {
              id: elem.id,
              event: elem.event
            }
          });

          buildChart(
            Math.ceil(Math.sqrt(objs.length)),
            Math.floor(Math.sqrt(objs.length)),
            objs
          );
        }
      })

    }
  }

});


let colors = {
  'ONLINE': '#99FF63',
  'AVAILABLE': '#99FF63',
  'OFFLINE': '#ff6864',
  'UNAVAILABLE': '#CCC'
}

///////////////////////////////////////////////////////////////////////////
///////////////////////////// Mouseover functions /////////////////////////
///////////////////////////////////////////////////////////////////////////

//Function to call when you mouseover a node
function mover(d) {
  let id = d3.select(this).attr('id');
  let nome = "restaurante_" + id.split("_")[1];
  Ember.$('#restaurante-legenda').html(nome);
  let el = d3.select(this)
    .transition()
    .duration(10)
    .style("fill-opacity", 0.3)
  ;
}

//Mouseout function
function mout(d) {
  Ember.$('#restaurante-legenda').html('&nbsp;');
  let el = d3.select(this)
    .transition()
    .duration(1000)
    .style("fill-opacity", 1)
  ;
};

///////////////////////////////////////////////////////////////////////////
////////////// Initiate SVG and create hexagon centers ////////////////////
///////////////////////////////////////////////////////////////////////////

//svg sizes and margins
let margin = {
  top: 120,
  right: 20,
  bottom: 20,
  left: 120
};

//The next lines should be run, but this seems to go wrong on the first load in bl.ocks.org
//let width = $(window).width() - margin.left - margin.right - 40;
//let height = $(window).height() - margin.top - margin.bottom - 80;
//So I set it fixed to:


function buildChart(columns, rows, items) {
  let width = 2000;
  let height = 700;

  console.log("columns", columns, "rows", rows, "items", items);

  //The number of columns and rows of the heatmap
  // let MapColumns = 36,
  // 	MapRows = 36;

  // let MapColumns = Math.ceil(Math.sqrt(objs.length)),
  // 	MapRows = Math.floor(Math.sqrt(objs.length));

  let MapColumns = columns,
    MapRows = rows;


  //The maximum radius the hexagons can have to still fit the screen
  let hexRadius = d3.min([width / (Math.sqrt(3) * (MapColumns + 3)),
    height / ((MapRows + 3) * 1.5)]);

  //Set the new height and width based on the max possible
  width = MapColumns * hexRadius * Math.sqrt(3);
  height = MapRows * 1.5 * hexRadius + 0.5 * hexRadius;

  //Set the hexagon radius
  let hexbin = d3.hexbin()
    .radius(hexRadius);

  //Calculate the center positions of each hexagon
  let points = [];
  let truePoints = [];
  for (let i = 0; i < MapRows; i++) {
    for (let j = 0; j < MapColumns; j++) {
      points.push([hexRadius * j * 1.75, hexRadius * i * 1.5]);
      truePoints.push([hexRadius * j * Math.sqrt(3), hexRadius * i * 1.5]);
    }//for j
  }//for i

  //Create SVG element
  let svg = d3.select("#chart").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  ///////////////////////////////////////////////////////////////////////////
  ////////////////////// Draw hexagons and color them ///////////////////////
  ///////////////////////////////////////////////////////////////////////////

  //Start drawing the hexagons
  svg.append("g")
    .selectAll(".hexagon")
    .data(hexbin(points))
    .enter().append("path")
    .attr("class", "hexagon")
    .attr("id", function (d, i) {
      return "ID_" + (items[i] ? items[i].id : "NODATA_" + i)
    })
    .attr("d", function (d) {
      return "M" + d.x + "," + d.y + hexbin.hexagon();
    })
    .attr("stroke", "white")
    .attr("stroke-width", "1px")
    .style("fill", function (d, i) {
      let evt = (items[i] ? items[i].event : "NODATA");
      return colors[evt] ? colors[evt] : "#FFF";
    })
    .on("mouseover", mover)
    .on("mouseout", mout);

}
