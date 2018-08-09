import React, { Component } from 'react';
import {  Cartesian3, BoundingSphere, Color } from "cesium/Cesium";
import {  Entity } from "cesium-react";
import Cesium from 'cesium';
import getColorFromGradient from 'gpotter-gradient';
import {figureEnum} from '../constants/visualization';

import PropTypes from 'prop-types';

const gradient = {
    0: '#ADD8E6',
    100: '#FF2500'
  };

class Visualizer extends Component {
    constructor(props) {
        super(props);
        this.state={
            data:this.props.data,
            pointShape:figureEnum.CUBE,
            transparency:1,
            minValue:0
        }
        this.emitter = this.props.emitter;

        this.changePointShape = this.changePointShape.bind(this);
        this.changeTransparency = this.changeTransparency.bind(this);
        this.changeMinValue = this.changeMinValue.bind(this);

    }


    componentDidUpdate(prevProps, prevState) {
        if (this.props.pointShape !== prevProps.pointShape) {
            this.setState({pointShape:this.props.pointShape});
        }
        if (this.props.data !== prevProps.data) {
            this.setState({data:this.props.data});
        }
    }

    logScale(b, n) {
        return Math.pow(Math.pow(Math.log(n) / Math.log(b), 2), 3);
    }

    valueToColor(value, max_value){
        const color = getColorFromGradient(gradient, (value/max_value) * 100); // #882737
        
        var r, g, b;

        r = parseInt(color.substring(1, 3), 16);
        g = parseInt(color.substring(3, 5), 16);
        b = parseInt(color.substring(5), 16);
        
        return {r, g, b};
    }

    componentWillMount() {
        this.emitter.on('pointShapeChanged', this.changePointShape);
        this.emitter.on('transparencyChanged', this.changeTransparency);
        this.emitter.on('minValueChanged', this.changeMinValue);   
    }

    changePointShape (pointShape) {
        this.setState({pointShape});
    }

    changeTransparency (transparency) {
        this.setState({transparency});
    }

    changeMinValue (minValue) {
        this.setState({minValue});
    }

    render() {
        var cube = null;
        var sphere = null;
        var cubePosition =  null;
        var color = null;
        const self = this;

        if(this.state.data){

            return (
                <div>
                    {this.state.data.result.map((element, index) => {
                        cube = new Cesium.Cartesian3(50,50,50);
                        sphere = new Cesium.Cartesian3(25,25,25);
                        cubePosition =  Cartesian3.fromDegrees(element.lon, element.lat, element.z);
                        const rgbColor = self.valueToColor(element.value, self.state.data.max_value);
                        const a = this.state.transparency;
                        // const a = self.logScale(element.value, self.state.data.max_value);
                        if(element.value < this.state.minValue)
                            return null;
                        color = new Cesium.Color(rgbColor.r/255, rgbColor.g/255, rgbColor.b/255, a) ;

                        if(self.state.pointShape == figureEnum.CUBE)
                            return (<Entity
                            key={index}
                            name = {'Gas concentration'}
                            description={element.value}
                            position={cubePosition}
                            box = {{
                                dimensions : cube,
                                material:color,
                            }}
                            >
                            </Entity>)
                        else
                            return (<Entity
                                key={index}
                                name = {'Gas concentration'}
                                description={element.value}
                                position={cubePosition}
                                ellipsoid = {{
                                    radii : sphere,
                                    material: color,
                                }}
                                >
                                </Entity>)
                     })} 
                    
                </div>
            );
        } else{
            return null;
        }
    }
}

Visualizer.propTypes = {

};

export default Visualizer;