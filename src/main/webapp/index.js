$(document).ready(function () {
    buildHistory('HISTORY');

    var $logArea = $(".tab-content > #ALL")
    var $logList = $(".tab-content > #ALL > .log-area > .log-list")
    var url = "HISTORY/ALL"
    makeAjax(url, function(data) {
        addLogTabData($logArea, $logList, data, url)
        createMap(parseLogs(data))
    })


    buildMetadata('RANK');
})

function makeAjax(path, callback) {
    var logpath = window.location.pathname.indexOf('honeypot') > -1 ? '/honeypot-1.0/' + path : path
    $.get(logpath, callback)
}

function buildMetadata(prefix) {
    $(".rank").each(function(key, $parent) {
        var id = $parent.id
        makeAjax(prefix + "/" + id, function(metadata) {
            var data = []
            $.each(metadata, function (key, value) {
                data.push({key: key, value: value})
            })
            data.sort(function (a, b) {
                return b.value - a.value
            })

            data = data.slice(0, 20)
            d3.select("#" + id  + " .chart").selectAll("div")
                .data(data)
                .enter()
                .append("div")
                .style("width", function (d) {
                    return Math.log2(d.value) * 15 + "px"
                })
                .text(function (d) {
                    return shorten(d.key) + ": " + d.value
                })
        })
    })
}
function shorten(str) {
    return str.length > 25 ? str.substring(0,25) + "..." : str
}

function buildHistory(prefix) {
    $(".logTabs > li > a").click(function() {
        var id = $(this)[0].innerHTML
        var $toClose = $(".tab-content > .active")
        $toClose.removeClass("active")

        var $logArea = $(".tab-content > #" + id)
        $logArea.addClass("active")
        var $logList = $(".tab-content > #" + id + " > .log-area > .log-list")
        var url = prefix + "/" + id
        $logList.empty()
        console.log(url)
        makeAjax(url, function(data) {
            addLogTabData($logArea, $logList, data, url)
            createMap(parseLogs(data))
        })
    })
}

function addLogTabData(parent, loglist, logs, url) {
    markers = []
    for (var i = logs.length - 1; i >= 0; i--) {
        var log = logs[i]

        var innerDiv = $('<div/>')
            .addClass('hidden')
            .addClass('log-info')
            .data("idx", log.idx)

        var newElm = $('<li/>')
            .addClass('log')
            .text('(' + log['type'] + ') ' + log.addr + '::' + log['port'])
            .append(innerDiv)

        loglist.append(newElm)
    }

    parent.find('.log').click(function (event) {
        var $innerDiv = $(this).find('div')
        if ($innerDiv.hasClass("hidden")) {
            makeAjax(url + '?idx=' + $innerDiv.data("idx"), function(data) {
                $innerDiv.html(dispJson(data))
            })
        }
        $innerDiv.toggleClass('hidden')
    })
}
function dispJson(json) {
    var attrs = []
    $.each(json, function (key, value) {
        attrs.push(key + ":\t" + value)
    })
    return attrs.join('<br>')
}

function parseLogs(logs) {
    var addressFrequencyMapping = {}
    var addressLocationMapping = {}

    for (var i = 0; i < logs.length; i++) {
        var log = logs[i];
        var address = log.addr;
        var coords = [parseFloat(log.lat), parseFloat(log.lon)]
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



