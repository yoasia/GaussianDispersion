import React from 'react';
import EventEmitter from 'event-emitter';
import axios from 'axios';
import { saveAs } from 'file-saver/FileSaver';

//aaaaa
import Map from './Map';
import {GAS} from '../constants/constants';

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
        this.createGeoJSON = this.createGeoJSON.bind(this);
    }

    componentWillMount() {
        this.emitter.on('reset', this.deleteData);
        this.emitter.on('onGeoJSONDownload', this.createGeoJSON);
    }

    deleteData(){
        this.setState({data:null, downloaded:false})
    }

    createGeoJSON(){
        var geoJSON = {
            type: "FeatureCollection",
            features:null
        };
        var features = [];
        var data2d = {};

        //Add up data (geoJSON is in 2d) 
        for (let index = 0; index < Object.keys(this.state.data.result).length; index++) {
            for (const point in this.state.data.result['range'+index]) {
                if (this.state.data.result['range'+index].hasOwnProperty(point)) {
                    const element = this.state.data.result['range'+index][point];

                    if(data2d[element.lat]){
                        if(data2d[element.lat][element.lon]){
                            data2d[element.lat][element.lon] += element.value;
                        }
                        else{
                            data2d[element.lat][element.lon] = element.value;
                        }
                    }
                    else{
                        data2d[element.lat] = {};
                        data2d[element.lat][element.lon] = element.value;
                    }
                }
            }
        }

        //Create geoJSON content
        for (const keyLat in data2d) {
            if (data2d.hasOwnProperty(keyLat)) {
                const object = data2d[keyLat];
                for (const keyLon in object) {
                    if (object.hasOwnProperty(keyLon)) {
                        const value = object[keyLon];
                        features.push(
                            {
                                "type": "Feature",
                                "geometry": {
                                  "type": "Point",
                                  "coordinates": [parseFloat(keyLon), parseFloat(keyLat)]
                                },
                                "properties": {
                                  "value": value
                                }
                            }  
                        )
                    }
                }
            }
        }

        geoJSON.features = features;

        this.saveFile(geoJSON);
    }

    saveFile(file){
        var blob = new Blob([JSON.stringify(file, null, 2)], {type: "application/geo+json"});
        saveAs(blob, "gas_despersion.json");
    }

    getData(){
        var self = this;
       axios.get('/Server/gauss', {
    //   axios.get('/mockup/3d'+Math.floor(Math.random() * 2 + 3)+'.json', {
            params: {
                wind_speed: this.props.parameters.windSpeed,
                wind_angle: this.props.parameters.windDirection,
                h: this.props.parameters.height,
                src_str:this.props.parameters.sourceStrength*3600*this.props.parameters.time,
                reffl: this.props.parameters.refflectionCo,
                stb_class: this.props.parameters.weatherStabilityClass,
                grid: this.props.parameters.grid,
                lon: this.props.parameters.lon,
                lat: this.props.parameters.lat,
                dimension_h:this.props.parameters.outputH,
                dimension: this.props.parameters.calculationArea,
                gas:GAS[this.props.parameters.gas]
            }
        }).then(function (response) {
            console.log(response);
            self.calculateCenterLine(self.props.parameters.windDirection);
            self.setState({downloaded:true, data:response.data});
            self.emitter.emit("dataDownloaded", response.data);
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