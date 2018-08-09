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
            centerLine:{
                A:null,
                B:null,
                C:null
            },
            downloaded:false
        };  
        this.emitter = this.props.emitter;
        this.getData = this.getData.bind(this);
        this.deleteData = this.deleteData.bind(this);
        this.calculateCenterLine = this.calculateCenterLine.bind(this);
    }

    componentWillMount() {
        this.emitter.on('reset', this.deleteData);
    }

    deleteData(){
        this.setState({data:null, downloaded:false})
    }

    getData(){
        var self = this;
        axios.get('/Server/gauss', {
        // axios.get('/mockup/3d'+Math.floor(Math.random() * 2 + 1)+'.json', {
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
                lat: this.props.parameters.lat,
                _3d: true
            }
        }).then(function (response) {
            console.log(response);
            self.calculateCenterLine(this.props.parameters.windDirection);
            self.setState({downloaded:true, data:response.data});
            self.emitter.emit("dataDownloaded", response.data.max_value);
        }).catch(function (error) {
            console.log(error);
            self.emitter.emit("dataDownloaded", false);
        });
    }

    calculateCenterLine(angle){
        if(angle > 90){
            angle -= 180;
        }
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