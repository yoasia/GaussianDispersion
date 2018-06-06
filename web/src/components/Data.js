import React from 'react';
import EventEmitter from 'event-emitter';
import axios from 'axios';

import Map from './Map'

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
        axios.get('/gauss', {
            params: {
                wind_speed: this.props.parameters.windSpeed,
                wind_angle: this.props.parameters.windDirection,
                h: this.props.parameters.height,
                src_str:this.props.parameters.sourceStrength,
                reffl: this.props.parameters.refflectionCo,
                stb_class: this.props.parameters.weatherStabilityClass,
                grid: this.props.parameters.grid,
                dimension: this.props.parameters.areaDimension
            }
        }).then(function (response) {
            console.log(response);
            self.setState({downloaded:true, data:response.data})
        }).catch(function (error) {
            console.log(error);
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