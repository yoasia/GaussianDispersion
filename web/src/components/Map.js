import React from 'react';
import {  Cartesian3, Color } from "cesium/Cesium";
import { Viewer, Entity, PointPrimitive } from "cesium-react";
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

        const markerPosition =  Cartesian3.fromDegrees(this.state.marker.lon, this.state.marker.lat);
        return (
            <Viewer full>
                 <Entity
                    name="source"
                    position={markerPosition}
                    point={{ pixelSize: 10 }}>
                    </Entity>
            </Viewer>
          );
    }
}

export default Map;