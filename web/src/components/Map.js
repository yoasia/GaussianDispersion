import React from 'react';
import { Map as MapLeaflet, Marker, Popup, TileLayer } from 'react-leaflet';
import EventEmitter from 'event-emitter';
import Visualizer from './Visualizer';

class Map extends React.Component {

    constructor(props) {
        super(props);
        
        this.state = {
            center: {
              lat: 52,
              lon: 20,
            },
            marker: {
                lat: 52,
                lon: 20,
            },
            zoom: 7,
            draggable: true,
            data:this.props.data
          }   
          this.map = null;
          this.emitter = this.props.emitter;

          this.setMarkerLonLat = this.setMarkerLonLat.bind(this);
          this.setDraggable = this.setDraggable.bind(this);
     
    }
    refmarker = React.createRef()
    componentWillMount() {
        this.emitter.on('lonLatChanged', this.setMarkerLonLat);
        this.emitter.on('draggableMarker', this.setDraggable);
    }
    
    setMarkerLonLat(lon, lat){
        var marker = this.state.marker;
        marker.lon = lon;
        marker.lat = lat;

        this.setState({marker});
    }

    componentDidMount() {
        this.map;    
    }

    /**
     * 
     * @param {boolean} draggable 
     */
    setDraggable (draggable) {
        this.setState({draggable})
    }

    toggleDraggable = () => {
        this.setState({ draggable: !this.state.draggable })
    }

    updatePosition = () => {
        const { lat, lng } = this.refmarker.current.leafletElement.getLatLng()
        const lon = lng;
        this.setState({
            marker: { lat, lon },
        })

        this.emitter.emit("lonLatChanged", lon, lat);
    }

    componentDidUpdate(prevProps, prevState) {
        if(prevProps.data != this.props.data){
            this.setState({data: this.props.data});
        }
    }

    render() {

        const position = [this.state.center.lat, this.state.center.lon]
        const markerPosition = [this.state.marker.lat, this.state.marker.lon]

        return (
            <MapLeaflet ref={(map)=>this.map = map} center={position} zoom={this.state.zoom}>     
                <TileLayer url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                    attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
                    />
                <Marker
                draggable={this.state.draggable}
                onDragend={this.updatePosition}
                position={markerPosition}
                ref={this.refmarker}>
                <Popup minWidth={90}>
                    <span onClick={this.toggleDraggable}>
                    {this.state.draggable ? 'DRAG MARKER' : 'MARKER FIXED'}
                    </span>
                </Popup>
                {(this.state.data != null) ? <Visualizer data={this.state.data}/> : null}
                </Marker>
            </MapLeaflet>
        );
    }
}

export default Map;