import React from 'react';
import ReactDOM from 'react-dom';  
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
        
        this.state = { 
            parameters:null
        };

        this.emitter = new EventEmitter();
        this.setUserParameters = this.setUserParameters.bind(this);
    }

    setUserParameters(parameters){
        this.setState({parameters});
    }

    render() {
        return (
            <div id='app'>
                 <Data emitter={this.emitter} parameters={this.state.parameters}></Data>
                 <Steps onFinish={this.setUserParameters} emitter={this.emitter} className={'panel'}></Steps>
            </div>      
        );
    }
}

ReactDOM.render(<App></App>, document.getElementById('root'));