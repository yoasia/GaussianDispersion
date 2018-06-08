import React, { Component } from 'react';
import PropTypes from 'prop-types';
import HeatmapLayer from 'react-leaflet-heatmap-layer';

class Visualizer extends Component {
    constructor(props) {
        super(props);
        this.state={
            data:this.props.data,
        }
    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {

    }

    shouldComponentUpdate(nextProps, nextState) {

    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {
        if(this.props != prevProps)
            this.setState({data: this.props.data})
    }

    componentWillUnmount() {

    }

    render() {
        if(this.state.data){

            return (
                <HeatmapLayer
                fitBoundsOnLoad
                fitBoundsOnUpdate
                points={this.state.data}
                longitudeExtractor={m => m["lon"]}
                latitudeExtractor={m => m["lat"]}
                intensityExtractor={m => parseFloat(m["value"])} />
            );
        } else
            return null;
    }
}

Visualizer.propTypes = {

};

export default Visualizer;