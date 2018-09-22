import React, { Component } from 'react';
import {  Cartesian3, BoundingSphere, Color } from "cesium/Cesium";
import {  Entity } from "cesium-react";
import Cesium from 'cesium';
import {figureEnum} from '../constants/visualization';
import {gradient} from '../constants/visualization';
import {valueToColor} from '../constants/visualization';

import PropTypes from 'prop-types';

class Visualizer extends Component {
    constructor(props) {
        super(props);
        this.state={
            data:this.props.data,
            pointShape:figureEnum.CUBE,
            transparency:1,
            minValue:0,
            currentLayer:null
        }
        this.emitter = this.props.emitter;

        this.changePointShape = this.changePointShape.bind(this);
        this.changeTransparency = this.changeTransparency.bind(this);
        this.changeMinValue = this.changeMinValue.bind(this);
        this.changeDisplayedLayer = this.changeDisplayedLayer.bind(this);

    }


    componentDidUpdate(prevProps, prevState) {
        if (this.props.pointShape !== prevProps.pointShape) {
            this.setState({pointShape:this.props.pointShape});
        }
        if (this.props.data !== prevProps.data) {
            if(this.props.data)
                this.setState({data:this.props.data,             
                    currentLayer:0
                });
            else
                this.setState({data:this.props.data,             
                    currentLayer:null
                });
        }
    }

    logScale(b, n) {
        return Math.pow(Math.pow(Math.log(n) / Math.log(b), 2), 3);
    }


    componentWillMount() {
        this.emitter.on('pointShapeChanged', this.changePointShape);
        this.emitter.on('transparencyChanged', this.changeTransparency);
        this.emitter.on('minValueChanged', this.changeMinValue);   
        this.emitter.on('displayedLayerChanged', this.changeDisplayedLayer);   
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

    changeDisplayedLayer (currentLayer) {
        this.setState({currentLayer});
    }

    render() {
        var cube = null;
        var sphere = null;
        var cubePosition =  null;
        var color = null;
        var boxes = [];
        const self = this;
        var grid;

        if(false){
        // if(this.state.data){
            grid = this.state.data.grid;
            for (let i = 0; i <= Object.keys(self.state.data.result).length - 1 - this.state.currentLayer; i++) {
                this.state.data.result['range'+i].forEach((element, index) => {
                    cube = new Cesium.Cartesian3(grid, grid, grid);
                    sphere = new Cesium.Cartesian3(25,25,25);
                    cubePosition =  Cartesian3.fromDegrees(element.lon, element.lat, element.z);
                    const rgbColor = valueToColor(i*(self.state.data.max_value/Object.keys(self.state.data.result).length), self.state.data.max_value);
                    const a = this.state.transparency;
                    // const a = self.logScale(element.value, self.state.data.max_value);
                    if(element.value < this.state.minValue)
                        return null;
                    color = new Cesium.Color(rgbColor.r/255, rgbColor.g/255, rgbColor.b/255, a) ;

                    if(self.state.pointShape == figureEnum.CUBE)
                        boxes.push((<Entity
                            key={i+"_"+index}
                            name = {'Gas concentration'}
                            description={element.value}
                            position={cubePosition}
                            box = {{
                                dimensions : cube,
                                material:color,
                            }}
                            >
                            </Entity>))
                    else
                        boxes.push((<Entity
                            key={+"_"+index}
                            name = {'Gas concentration'}
                            description={element.value}
                            position={cubePosition}
                            ellipsoid = {{
                                radii : sphere,
                                material: color,
                            }}
                            >
                            </Entity>))
                 })
                
            }

            return (
                <div>
                    {boxes} 
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