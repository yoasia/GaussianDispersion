import React from 'react';
import EventEmitter from 'event-emitter';
import axios from 'axios';

import Map from './Map';

class Data extends React.Component {

    constructor(props) {
        super(props);
        
        this.state = { 
            parameters: this.props.parameters,
            data:null,
            downloaded:false
        };  
        this.emitter = this.props.emitter;
        this.getData = this.getData.bind(this);
    }

    getData(){
        var self = this;
        // axios.get('/Server/gauss', {
        axios.get('/mockup/3d.json', {
            params: {
                wind_speed: this.props.parameters.windSpeed,
                wind_angle: this.props.parameters.windDirection,
                h: this.props.parameters.height,
                src_str:this.props.parameters.sourceStrength,
                reffl: this.props.parameters.refflectionCo,
                stb_class: this.props.parameters.weatherStabilityClass,
                grid: this.props.parameters.grid,
                dimension: this.props.parameters.areaDimension,
                lon: this.props.parameters.lon,
                lat: this.props.parameters.lat
            }
        }).then(function (response) {
            console.log(response);
            self.setState({downloaded:true, data:response.data})
            self.emitter.emit("dataDownloaded", true);
        }).catch(function (error) {
            console.log(error);
            self.emitter.emit("dataDownloaded", false);
        });
    }

    componentDidUpdate(prevProps, prevState) {
        
        if(prevProps.parameters != this.props.parameters){
            this.setState({parameters:this.props.parameters});
            if(this.props.parameters){
                this.getData();
            }
        }
    }

    render() {
        return (
            <Map emitter={this.emitter} parameters={this.state.parameters} data={this.state.data}></Map>
        );
    }
}

export default Data;