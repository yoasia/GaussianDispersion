import React from 'react';
import {  Cartesian3, BoundingSphere, Color } from "cesium/Cesium";
import Cesium from 'cesium';
 
import { Viewer, Entity, Camera, CameraFlyToBoundingSphere, KmlDataSource, GeoJsonDataSource } from "cesium-react";
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
        const boundingSphere = new BoundingSphere( markerPosition, 2000);
        const sourcePoint = new Cesium.Cartesian3(50, 50, 50);
        const color = new Cesium.Color(0.5, 0.25, 0.5, 0.5);

        return (
            <Viewer full  ref={e => {
                this.viewer = e ? e.cesiumElement : null;
              }}>
                <Visualizer data={this.state.data} />
                <Camera
                    view={{
                    destination: markerPosition,
                    }}
                />
                <Entity
                name = {'Source point'}
                position={markerPosition}
                ellipsoid = {{
                    radii : sourcePoint,
                    material:color,
                   }}
                >
                </Entity>
                {/* camera animation */}
                <CameraFlyToBoundingSphere
                    boundingSphere={boundingSphere}
                    duration={1}
                />
            </Viewer>
          );
    }
}

export default Map;