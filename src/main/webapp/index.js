$(document).ready(function () {
    var logs = ["ALL", "SSH", "HTTP", "SMTP", "IRC"]
    // tabs and click handlers
    logs.forEach(function (key) {
        addTab(key)
    })


    var logpath = window.location.pathname.indexOf('honeypot') > -1 ? '/honeypot-1.0/log/TOP_COUNTRIES' : '/log/TOP_COUNTRIES'
    $.get(logpath, function (logObj) {
        console.log(logObj)
        setMetadata(logObj["TOP_COUNTRIES"][0])
    })
})

function setMetadata(metadata) {
    var data = []
    $.each(metadata, function (key, value) {
        data.push({key: key, value: value})
    })
    data.sort(function (a, b) {
        return a.value - b.value
    })

    var nums = []
    data.forEach(function (obj) {
        nums.push(obj.value)
    })


    d3.select(".chart").selectAll("div")
        .data(data)
        .enter()
        .append("div")
        .style("width", function (d) {
            return d.value * 10 + "px"
        })
        .text(function (d) {
            return d.key + ": " + d.value
        })

}

function addTab(name) {
    var logpath = window.location.pathname.indexOf('honeypot') > -1 ? '/honeypot-1.0/log/' + name : '/log/' + name
    var elm = $("<li role='presentation' class=''><a href='#" + name + "' aria-controls='" + name + "' role='tab' data-toggle='tab'>" + name + "</a></li>")
    elm.click(function () {
        $.get(logpath, function (logObj) {
            var logs = logObj[name]
            setDataForType(name, logs)
            createMap(parseLogs(logs))
        })
    })
    if (name == "ALL") {
        $.get(logpath, function (logObj) {
            var logs = logObj[name]
            setDataForType(name, logs)
            createMap(parseLogs(logs))
        })
    }
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


function addLogTabData(parent, logs) {
    markers = []
    var loglist = parent.find('.log-list')
    loglist.empty()
    for (var i = logs.length - 1; i >= 0; i--) {
        var log = logs[i]

        var attrs = []
        $.each(log, function (key, value) {
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

    parent.find('.log').click(function (event) {
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
    $.each(addressLocationMapping, function (key, value) {
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
    var mean = calcMean(data.dataList)
    var sigma = calcSigma(data.dataList, mean)

    this.onMarkerTipShow = function (event, label, index) {
        label.html(
            '<b>' + data.addressList[index] + '</b><br/>' +
            '<b>Connections: </b>' + data.dataList[index] + '</br>'
        )
    }

    if (this.map) {
        this.map.removeAllMarkers()
        this.map.addMarkers(data.coordList, [])
        this.map.series.markers[0].setValues(data.dataList)
        this.map.series.markers[1].setValues(data.dataList)
    } else {
        $('.map').vectorMap({
            map: 'world_mill',
            normalizeFunction: 'polynomial',
            hoverOpacity: 0.4,
            hoverColor: false,
            markerStyle: {
                initial: {
                    fill: '#FF122B',
                    'fill-opacity': 0.5,
                    r: 3,
                    stroke: '#383f47'
                }
            },
            backgroundColor: '#383f47',
            markers: data.coordList,
            series: {
                markers: [
                    {
                        attribute: 'fill',
                        scale: ['#FF0000', '#2F0000'],
                        values: data.dataList,
                        min: Math.max(mean - sigma * 3, 0),
                        max: Math.min(mean + sigma * 3, jvm.max(data.dataList))
                    },
                    {
                        attribute: 'r',
                        scale: [3, 15],
                        values: data.dataList,
                        min: Math.max(mean - sigma * 3, 0),
                        max: mean + Math.min(mean + sigma * 3, jvm.max(data.dataList))
                    }]
            },

            onMarkerTipShow: this.onMarkerTipShow,
        });

        this.map = $('.map').vectorMap('get', 'mapObject')
    }
}

function calcMean(list) {
    if (list.length == 0)
        return 0

    var size = list.length

    var total = list[0];
    for (var i = 0; i < size; i++)
        if (list[i] < 1000)
            total += list[i]

    return total / size
}

function calcSigma(list, mean) {
    if (!mean)
        mean = calcMean(list)

    var size = list.length;

    var totalDev = 0
    for (var i = 0; i < size; i++) {
        var dev = Math.abs(mean - list[i])
        if (list[i] < 1000)
            totalDev += dev
    }

    return totalDev / size
}



