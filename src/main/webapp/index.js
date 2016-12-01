$(document).ready(function() {
    setHistoryData()

    // setDataForType('ALL')
    //
    // $('.logTabs a').click(function (e) {
    //   e.preventDefault()
    //
    //   setDataForType($(this)[0].innerHTML)
    // })

})

function setHistoryData() {
    var logpath = window.location.pathname.indexOf('honeypot') > -1 ? '/honeypot-1.0/log/HISTORY' : '/log/HISTORY'
    $.get(logpath, function(logs) {
        console.log(logs)
        createMap(parseLogs(logs['ALL']))

        $.each(logs, function(key, value) {
            addTab(key)
            setDataForType(key, value)
        })
        //addLogTabData(parent, logs)
    })
}
function addTab(name) {
    var elm = $("<li role='presentation' class=''><a href='#" +name+ "' aria-controls='" +name+ "' role='tab' data-toggle='tab'>"+ name + "</a></li>")
    $(".logTabs").append(elm)
}

function setDataForType(type, logs) {

    var elm = $(
"<div role='tabpanel' class='tab-pane' id='" + type + "'>" +
"<div class='log-area'>" +
"<ul class='log-list'>" +
"</ul>" +
"</div>" +
"</div>")

    $('.tab-content').append(elm)

    var parent = $("#" + type)
    addLogTabData(parent, logs)
}


function setMetadata(metadata) {
    str = ''
    $.each(metadata, function(key, value) {
        str += key + ": " + value + "<br>"
    })

    $('.metadata').html(str)
}

function addLogTabData(parent, logs) {
    markers = []
    var loglist = parent.find('.log-list')
    loglist.empty()
    for(var i = logs.length-1; i >= 0; i--) {
        var log = logs[i]

        var attrs = []
        $.each(log, function(key, value) {
            attrs.push(key + ":\t" + value)
        })

        var innerDiv = $('<div/>')
            .addClass('hidden')
            .addClass('log-info')
            .html(attrs.join('<br>'))

        var newElm = $('<li/>')
            .addClass('log')
            .text('(' + log['event-type'] + ') ' + log.address + '::' + log['remote-port'])
            .append(innerDiv)

        loglist.append(newElm)
    }

    parent.find('.log').click(function(event) {
        $(this).find('div').toggleClass('hidden')
    })
}

function parseLogs(logs) {
    var addressFrequencyMapping = {}
    var addressLocationMapping = {}

    for (var i = 0; i < logs.length; i++) {
        var log = logs[i];
        var address = log.address;
        var coords = [parseFloat(log.latitude), parseFloat(log.longitude)]
        if (!addressFrequencyMapping[address] && addressFrequencyMapping[address] != 0)
            addressFrequencyMapping[address] = 1
        else
            addressFrequencyMapping[address]++

        addressLocationMapping[address] = coords
    }

    var coordList = []
    var dataList = []
    var addressList = []
    $.each(addressLocationMapping, function(key, value) {
        coordList.push(value)
        dataList.push(addressFrequencyMapping[key])
        addressList.push(key)
    })

    return {
        'coordList': coordList,
        'dataList': dataList,
        'addressList': addressList,
    }
}

function createMap(data) {
  // map.addMarker(), can add series data and key also
  // map.addMarkers()

  this.onMarkerTipShow = function(event, label, index) {
    label.html(
        '<b>'+data.addressList[index]+'</b><br/>'+
        '<b>Connections: </b>'+data.dataList[index]+'</br>'
      )
    }

  if (this.map) {
    this.map.removeAllMarkers()

    this.map.addMarkers(data.coordList, data.dataList)
  } else {
      $('.map').vectorMap({
        map: 'world_mill',
        scaleColors: ['#C8EEFF', '#0071A4'],
        normalizeFunction: 'polynomial',
        hoverOpacity: 0.4,
        hoverColor: false,
        markerStyle: {
          initial: {
            fill: '#F8E23B',
            'fill-opacity': 1,
            stroke: '#383f47'
          }
        },
        backgroundColor: '#383f47',
        markers: data.coordList,
        series: {
            markers: [{
              attribute: 'fill',
              scale: ['#FEE5D9', '#A50F15'],
              values: data.dataList,
              min: 0,//jvm.min(data),
              max: 10//jvm.max(data)
            },
            {
              attribute: 'fill-opacity',
              scale: [1, 1],
              values: data.dataList,
              min: 0,//jvm.min(data),
              max: 100//jvm.max(data)
            },
            {
              attribute: 'r',
              scale: [5, 25],
              values: data.dataList,
              min: 0, //jvm.min(data),
              max: 100//jvm.max(data)
            }]
          },

          onMarkerTipShow: this.onMarkerTipShow,
      });

      this.map = $('.map').vectorMap('get', 'mapObject')
  }
}



