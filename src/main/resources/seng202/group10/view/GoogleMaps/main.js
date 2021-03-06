var map;
var javaConnector; // Placeholder
var markers = [];

var bounds_changed = false;
var flightLine;


/**
 * Handles/gives out labels for the markers
 */
class LabelHandler {
    constructor() {
        this.labelIndex = 0;
    }

    /**
     * Get a list of labels
     * @param {int} numLabels how many labels to get
     */
    getLabels(numLabels) {
        this.labelIndex = 0;
    }

    /**
     * Get the next label
     * @returns {[string, int]} the label and the index
     */
    getNextLabel() {
        return [this.labelIndex.toString(), this.labelIndex++];
    }

    /**
     * n = 0; return a
     * n = 1; return b
     * n = 26; return aa
     * n = 27; return ab
     * etc
     * @param {int} n
     */
    makeLetterLabel(n) {
        let s = "";

        s += String.fromCharCode(97 + n % 26);
        if (n > 26) {
            s += String.fromCharCode(97 + Math.floor(n / 26) - 1);
        }
        return s;
    }
}


/**
 * Wrapper for the google maps marker
 */
class MyMarker {

    constructor(label, labelIndex, latLng) {
        this.label = label;
        this.labelIndex = labelIndex;

        javaConnector.newMarker(label, latLng.lat(), latLng.lng());

        this.mapsMarker = new google.maps.Marker({
            position: latLng,
            label: this.label,
            map: map,
            draggable: true
        });

        let self = this;
        google.maps.event.addListener(this.mapsMarker, 'click', function(event) {
            removeMarker(self);
            drawPath();
        });

        google.maps.event.addListener(this.mapsMarker, 'drag', function(event) {
            javaConnector.moveMarker(self.label, self.mapsMarker.position.lat(), self.mapsMarker.position.lng());
            drawPath();
        });
    }

    getPosition() {
        return this.mapsMarker.getPosition();
    }

    delete() {
        this.mapsMarker.setMap(null);
    }

    setLabel(newLabel, newLabelIndex) {
        this.label = newLabel;
        this.labelIndex = newLabelIndex;
        this.mapsMarker.setLabel(this.label);
    }
}


class Airport {
    constructor(data) {
        this.marker = new google.maps.Marker({
            position: new google.maps.LatLng(data.latitude, data.longitude),
            map: map,
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                strokeColor: "#0066ff",
                scale: 5
            }
        });
        var content = "<body>" +
            "<h2>" + data.name + "</h2><br>" +
            "<h4>" + data.city + ", " + data.country + "</h4><br>" +
            "<p>Altitude: " + data.altitude +"</p>" +
            "<button onclick='addMarker(new google.maps.LatLng("+ data.latitude +", " + data.longitude +"))'>Add to flight</button>" +
            "</body>";
        this.window = new google.maps.InfoWindow({
            content: content
        });
        this.marker.addListener('click', () => {
            this.window.open(map, this.marker);
        });
    }

    close() {
        this.marker.setMap(null);
    }
}

let labelHandler = new LabelHandler();


/**
 * Initialize the map
 * Add the listener for adding markers
 */
function initMap() {
    var ucPos = { lat: -43.522456, lng: 172.579422 };
    map = new google.maps.Map(document.getElementById('googleMap'), {
        center: new google.maps.LatLng(ucPos.lat, ucPos.lng),
        zoom: 10,
        mapTypeControl: false,
        streetViewControl: false
    });
    flightLine = new google.maps.Polyline();
    google.maps.event.addListener(map, 'click', function(event) {
        addMarker(event.latLng);
    });
    google.maps.event.addListenerOnce(map, 'tilesloaded', function() {
        sendBounds();
    });
    google.maps.event.addListener(map, 'bounds_changed', function() {
        bounds_changed = true;
    });
    google.maps.event.addListener(map, 'idle', function() {
        if (bounds_changed) {
            bounds_changed = false;
            removeAirports();
            sendBounds();
        }
    });
}


function newMarker(lat, lng) {
//    addMarker(new google.maps.LatLng(lat, lng));
    addMarker(new google.maps.LatLng(lat, lng));
}

function sendBounds() {
    var bounds = map.getBounds();
    javaConnector.setAirports(
        bounds.getNorthEast().lat(),
        bounds.getNorthEast().lng(),
        bounds.getSouthWest().lat(),
        bounds.getSouthWest().lng()
    );
}

/**
 * Make a new marker
 * tell java about it with sendLocationToJava
 * add it to the markers dict
 */
function addMarker(location) {
    // TODO give more than 26 markers
    let currentIndex = labelHandler.labelIndex;
    // var label = labels[labelIndex++];
    let [label, labelIndex] = labelHandler.getNextLabel();
    let marker = new MyMarker(label, labelIndex, location);
    markers[currentIndex] = marker
    drawPath();
    sendMarkersToJava();
}



function removeMarker(marker) {
    javaConnector.removeMarker(marker.label);
    // Remove from map
    marker.delete();
    // Remove from markers array
    markers.splice(marker.labelIndex, 1);
    // Reset marker labels
    labelHandler.labelIndex = 0;
    for (let i = 0; i < markers.length; i++) {
        // Change labels
        let [newLabel, newIndex] = labelHandler.getNextLabel()
        markers[i].setLabel(newLabel, newIndex);
    }
    labelIndex -= 1;
}


function removeAllMarkers() {
    for (let marker of markers) {
        marker.delete();
    }
    labelHandler.labelIndex = 0;
    markers = [];
    drawPath();
}


// Get a list of labels, lats and lngs to pass to java
function makeJavaMarkerLists() {
    let labels = [];
    let lats = [];
    let lngs = [];
    for (let marker of markers) {
        labels.push(marker.label);
        lats.push(marker.getPosition().lat());
        lngs.push(marker.getPosition().lng());
    }
    return [labels, lats, lngs];
}

function drawPath() {
    flightLine.setMap(null);
    flightLine = new google.maps.Polyline({
        strokeColor: "#00a9ff",
        strokeWeight: 2
    });
    path = flightLine.getPath();
    for (var i = 0; i < markers.length; i++) {
        path.push(markers[i].getPosition());
    }
    flightLine.setPath(path);
    flightLine.setMap(map);
}

/**
 * Print some text into the java console
 */
function println(text) {
    javaConnector.println(text);
}

var airports = [];
var airportIndex = 0;

function addAirport(inAirport) {
    airports[airportIndex] = new Airport(inAirport);
    airportIndex += 1;
}

function removeAirports() {
    for (var i = 0; i < airportIndex; i++) {
        airports[i].close();
    }
    airports = [];
    airportIndex = 0;
}

/**
 * Control vertical map resizing
 * Can't size to 100% in css so this how we gotta do it
 */
function resizeMap() {
    let mapElement = document.querySelector("#googleMap");
    mapElement.style.height = window.innerHeight.toString() + "px";
}

resizeMap(); // Initial size
window.onresize = resizeMap; // Listener
