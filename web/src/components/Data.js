import React from 'react';
import EventEmitter from 'event-emitter';

import Map from './Map'

class Data extends React.Component {

    constructor(props) {
        super(props);
        
        this.state = { };  
        this.emitter = this.props.emitter;
      
    }

    render() {
        return (
            <Map emitter={this.emitter}></Map>
        );
    }
}

export default Data;