$(document).ready(function() {
    // grab graph points
    $.get('/log/all', function(data) {
        markers = []
        for(var i = 0; i < data.length; i++) {
            event = data[i]
            markers.push({
                latLng: [parseFloat(event.latitude), parseFloat(event.longitude)],
                name: event.address
            })
        }
        createMap(markers)
    })
})

function createMap(markers) {
  $('.map').vectorMap({
    map: 'world_mill',
    scaleColors: ['#C8EEFF', '#0071A4'],
    normalizeFunction: 'polynomial',
    hoverOpacity: 0,
    hoverColor: false,
    markerStyle: {
      initial: {
        fill: '#F8E23B',
        stroke: '#383f47'
      }
    },
    backgroundColor: '#383f47',
    markers: markers
  });
}