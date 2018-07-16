import React, { Component } from 'react';
import {  Cartesian3, BoundingSphere, Color } from "cesium/Cesium";
import {  Entity } from "cesium-react";
import Cesium from 'cesium';
import PropTypes from 'prop-types';


class Visualizer extends Component {
    constructor(props) {
        super(props);
        this.state={
            data:this.props.data,
        }
    }

    componentDidUpdate(prevProps, prevState) {
        if(this.props != prevProps)
            this.setState({data: this.props.data})
    }

    componentWillUnmount() {

    }

    render() {
        var cube = null;
        var cubePosition =  null;
        var color = null;
        
        if(this.state.data){

            return (
                <div>
                    {this.state.data.map(element => {
                        cube = new Cesium.Cartesian3(50, 50, 50);
                        cubePosition =  Cartesian3.fromDegrees(element.lon, element.lat, element.z);
                        color = new Cesium.Color(0.5, 0.25, 0.5, 0.5);

                        return (<Entity
                        name = {'Gas concentration'}
                        position={cubePosition}
                        box = {{
                          dimensions : cube,
                          material:color,
                          outline:true,
                          outlineColor:Cesium.Color.color}}
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