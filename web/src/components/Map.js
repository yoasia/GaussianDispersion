import React from 'react';
import {  Cartesian3, BoundingSphere, Cartesian2 } from "cesium/Cesium";
 import Cesium from 'cesium';
 
import { Viewer, Entity, Camera, CameraFlyTo } from "cesium-react";
import Visualizer from './Visualizer';
import { BBOX_WIDTH } from '../constants/visualization';

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
                height:0
            },
            zoom: 7,
            draggable: true,
            data:this.props.data
          }
          this.animateCamera = true;
          this.emitter = this.props.emitter;

          this.setMarkerLonLat = this.setMarkerLonLat.bind(this);
          this.setDraggable = this.setDraggable.bind(this);
    }
    refmarker = React.createRef()

    componentWillMount() {
        this.emitter.on('lonLatChanged', this.setMarkerLonLat);
        this.emitter.on('draggableMarker', this.setDraggable);

    }
    
    setMarkerLonLat(lon, lat, height){
        var marker = Object.assign({}, this.state.marker);
        marker.lon = lon;
        marker.lat = lat;
        marker.height = height;

        this.setState({marker});
        this.animateCamera = true;
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
        this.animateCamera = true;
        this.emitter.emit("lonLatChanged", lon, lat);

    }

    componentWillUpdate(nextProps, nextState) {
        if(nextState.marker != this.state.marker)
            this.animateCamera = true;
        else
            this.animateCamera = false;

    }
    

    componentDidUpdate(prevProps, prevState) {
        if(prevProps.data != this.props.data){
            this.setState({data: this.props.data}); 
        }
        
    }

    render() {
        const markerPosition =  Cartesian3.fromDegrees(this.state.marker.lon, this.state.marker.lat, this.state.marker.height);
        const boundingSphere = new BoundingSphere( markerPosition, 2000);
        var rectangle = Cesium.Rectangle.fromDegrees(this.state.marker.lon - BBOX_WIDTH, this.state.marker.lat -  BBOX_WIDTH, this.state.marker.lon +  BBOX_WIDTH, this.state.marker.lat + BBOX_WIDTH);
        const sourcePoint = new Cesium.Cartesian3(20, 20, 20);
        const color = new Cesium.Color(0.5, 0.25, 0.5, 1);
        var cameraAnimation = null;
        var easingF = Cesium.EasingFunction.CUBIC_IN_OUT;

        if(this.animateCamera)
            cameraAnimation = (
                <div>
                    <CameraFlyTo
                    destination={rectangle}
                    duration={1}
                    easingFunction={easingF}
                    />
                </div>
            )

        return (
            <Viewer full  ref={e => {
                this.viewer = e ? e.cesiumElement : null;
              }}>
                <Visualizer emitter={this.emitter} data={this.state.data} />
                <Entity
                name = {'Source point'}
                label={{
                    id: 'source',
                    text: 'source',
                    verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
                    pixelOffset:new Cesium.Cartesian2(0, -20)
                }}
                position={markerPosition}
                ellipsoid = {{
                    radii : sourcePoint,
                    material: color,
                   }}
                >
                </Entity>
                {/* camera animation */}
                {cameraAnimation}
            </Viewer>
          );
    }
}

export default Map;