import React from 'react';
import ReactDOM from 'react-dom';  
import axios from 'axios';
import EventEmitter from 'event-emitter';

import Data from './components/Data';
import Steps from './components/Steps';

/**
 * Main Component.
 * @author Joanna Sienkiewicz
 */
class App extends React.Component {
    
    constructor(props) {
        super(props);
        
        this.state = { };
        this.emitter = new EventEmitter();
    }

    componentWillMount(){
        
    }

    render() {
        return (
            <div id='app'>
                 <Data emitter={this.emitter}></Data>
                 <Steps emitter={this.emitter} className={'panel'}></Steps>
            </div>      
        );
    }
}

ReactDOM.render(<App></App>, document.getElementById('root'));