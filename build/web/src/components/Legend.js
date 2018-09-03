import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import FormLabel from '@material-ui/core/FormLabel';
import FormControl from '@material-ui/core/FormControl';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormHelperText from '@material-ui/core/FormHelperText';
import Checkbox from '@material-ui/core/Checkbox';
import {valueToColor} from '../constants/visualization';

const styles = theme => ({
    root: {
      display: 'flex',
    },
    formControl: {
      margin: theme.spacing.unit * 3,
    },
  });

class Legend extends Component {

    constructor(props){

        super(props);
        this.state = {
            rangesNumber: this.props.rangesNumber,
            maxValue: this.props.maxValue,
            range:this.props.maxValue/this.props.rangesNumber,
            currentRange:0
        };

    }

    handleChange = name => event => {
        this.setState({ [name]: event.target.checked });
      };


    render() {
        var self = this;
        var checkboxes = [];
        const { classes } = this.props;


        for (let index = 0; index < this.state.rangesNumber; index++) {
            var color =  valueToColor(index*(self.state.maxValue/self.state.rangesNumber), self.state.maxValue);
            checkboxes.push(<FormControlLabel
                control={
                  <Checkbox 
                  checked={(self.state.currentRange > index)} 
                  onChange={this.handleChange(index)} 
                  value={index} 
                  classes={{
                    root:{color:'rgba('+color.r+',' +color.g+','+ color.b+',' +0.5+') ',
                    '&$checked': {
                        color: 'rgba('+color.r+',' +color.g+','+ color.b+',' +1+') ',
                    },}
                  }}
                  />
                }
                label={Math.floor(self.state.range * index)}
              />)
            
        }

        return (
            <div>
                <FormControl component="fieldset" >
                      <FormLabel component="legend">Legend</FormLabel>
                      <FormGroup>
                        {checkboxes}
                      </FormGroup>
                    </FormControl> 
            </div>
        );
    }
}

Legend.propTypes = {

};

export default Legend;