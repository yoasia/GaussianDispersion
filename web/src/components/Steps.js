import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import InputAdornment from '@material-ui/core/InputAdornment';
import MenuItem from '@material-ui/core/MenuItem';
import EventEmitter from 'event-emitter';


var emitter;

const weatherStability=[
    {
        value: 1,
        label: 'Very unstable',
    },
    {
        value: 2,
        label: 'Moderately unstable',
    },
    {
        value: 3,
        label: 'Slightly unstable',
    },
    {
        value: 4,
        label: 'Neutral',
    },
    {
        value: 5,
        label: 'Moderately stable',
    },
    {
        value: 6,
        label: 'Very stable',
    }
]

const styles = theme => ({
  root: {
    position: 'absolute',
    top: '0',
    right: '0',
    zIndex: '2',
    backgroundColor: 'white',
    width: '30%',
    padding:'10px'
  },
  button: {
    marginRight: theme.spacing.unit,
  },
  instructions: {
    marginTop: theme.spacing.unit,
    marginBottom: theme.spacing.unit,
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200,
  }
});

function getSteps() {
  return ['Gas source', 'Weather', 'Other parametrs'];
}

function emitEventDraggableMarker(step){
    if(step == 0){
        emitter.emit("draggableMarker", true);
    }
    else{
        emitter.emit("draggableMarker", false);
    }
  }



class Steps extends React.Component {

    constructor(props) {
        super(props);
        
        this.state = {
            activeStep: 0,
            skipped: new Set(),
            parameters:{
                lon:20, 
                lat:52,
                windSpeed:5,
                windDirection:30,
                height:2,
                sourceStrength:5,
                refflectionCo:1,
                weatherStabilityClass:1,
                areaDimension:null,
                grid:10
            }
          };
         
        this.textFieldValues = Object.assign({}, this.state.parameters);

        emitter = this.props.emitter;

        this.getStepContent = this.getStepContent.bind(this);
        this.setLonLat = this.setLonLat.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.onFinish = this.onFinish.bind(this);

    }

    
    static propTypes = {
        classes: PropTypes.object,
    };
    
    componentWillMount() {
        emitter.on('lonLatChanged', this.setLonLat);
    }

    onFinish(){
      this.props.onFinish(this.state.parameters);
    }
    
    /**
     * 
     * @param {number} lon 
     * @param {number} lat 
     */
    setLonLat(lon, lat){
        var parameters = this.state.parameters;

        [this.textFieldValues.lon, this.textFieldValues.lat] = [lon, lat];
        [parameters.lon, parameters.lat] = [lon, lat];

        this.setState({parameters});
    }

    /**
     * 
     * @param {string} name 
     */
    handleChange = name => event => {
        this.textFieldValues[name] = event.target.value;

        if(!isNaN(this.textFieldValues[name])){

            var parameters = this.state.parameters;
            parameters[name] = event.target.value;

            if(name == "lon" || name == "lat"){
                emitter.emit("lonLatChanged", parameters.lon, parameters.lat);
            } else{
                this.setState({parameters});
            }
    
        }
      };    


  getStepContent(step) {
    const { classes } = this.props;
    switch (step) {
      case 0:
        return (
        <form className={classes.container} noValidate autoComplete="off">
            <TextField
            id="lonTextField"
            label="Longitude"
            value={this.textFieldValues.lon}
            className={classes.textField}
            onChange={this.handleChange('lon')}
            margin="normal"
            error={(isNaN(this.textFieldValues.lon)? true:false )}
            />
            <TextField
            id="latTextField"
            label="Latitude"
            value={this.textFieldValues.lat}
            onChange={this.handleChange('lat')}
            className={classes.textField}
            margin="normal"
            error={(isNaN(this.textFieldValues.lat)? true:false )}
            />
            <TextField
            id="heightTextField"
            label="Height of the source"
            value={this.textFieldValues.height}
            onChange={this.handleChange('height')}
            className={classes.textField}
            margin="normal"
            error={(isNaN(this.textFieldValues.height)? true:false )}
            InputProps={{
                endAdornment: <InputAdornment position="end">m</InputAdornment>,
            }}
            />
            <TextField
            id="srcStrTextField"
            label="Gas emmission"
            value={this.textFieldValues.sourceStrength}
            onChange={this.handleChange('sourceStrength')}
            className={classes.textField}
            margin="normal"
            error={(isNaN(this.textFieldValues.sourceStrength)? true:false )}
            InputProps={{
                endAdornment: <InputAdornment position="end">grams/seconds</InputAdornment>,
            }}
            />
        </form>);
      case 1:
        return (
            <form className={classes.container} noValidate autoComplete="off">
                <TextField
                id="speedTextField"
                label="Wind speed"
                value={this.textFieldValues.windSpeed}
                className={classes.textField}
                onChange={this.handleChange('windSpeed')}
                margin="normal"
                error={(isNaN(this.textFieldValues.windSpeed)? true:false )}
                InputProps={{
                    endAdornment: <InputAdornment position="end">m/s</InputAdornment>,
                }}
                />
                <TextField
                id="directionTextField"
                label="Wind direction"
                helperText="to North = 0 degree "
                value={this.textFieldValues.windDirection}
                onChange={this.handleChange('windDirection')}
                className={classes.textField}
                margin="normal"
                error={(isNaN(this.textFieldValues.windDirection)? true:false )}
                InputProps={{
                    endAdornment: <InputAdornment position="end">degrees</InputAdornment>,
                }}
                />
                <TextField
                id="stabilityTextField"
                select
                label="Weather stability"
                className={classes.textField}
                value={this.textFieldValues.weatherStabilityClass}
                onChange={this.handleChange('weatherStabilityClass')}
                SelectProps={{
                    MenuProps: {
                        className: classes.menu,
                    },
                }}
                helperText="Select weather stability"
                margin="normal"
                >
                {weatherStability.map(option => (
                    <MenuItem key={option.value} value={option.value}>
                    {option.label}
                    </MenuItem>
                ))}
                </TextField>
                
            </form>);
      case 2:
        return (
            <form className={classes.container} noValidate autoComplete="off">
                <TextField
                id="gridTextField"
                label="Grid"
                value={this.textFieldValues.grid}
                className={classes.textField}
                type="number"
                onChange={this.handleChange('grid')}
                margin="normal"
                error={(isNaN(this.textFieldValues.grid)? true:false )}
                InputLabelProps={{
                    shrink: true,
                    inputProps: { min: 5, max: 200 }
                }}
                InputProps={{
                    endAdornment: <InputAdornment position="end">m</InputAdornment>,
                }}
                />
                </form>
        );
      default:
        return 'Unknown step';
    }
  }

  isStepOptional = step => {
    return step === 1;
  };

  isStepSkipped(step) {
    return this.state.skipped.has(step);
  }


  handleNext = () => {
    const { activeStep } = this.state;
    let { skipped } = this.state;
    emitEventDraggableMarker(activeStep + 1);
    
    if (this.isStepSkipped(activeStep)) {
        skipped = new Set(skipped.values());
        skipped.delete(activeStep);
    }
    this.setState({
        activeStep: activeStep + 1,
        skipped,
    });

    if(activeStep > 1){
      this.onFinish()
    }
  };

  handleBack = () => {
    const { activeStep } = this.state;
    emitEventDraggableMarker(activeStep - 1);

    this.setState({
      activeStep: activeStep - 1,
    });
  };

  handleSkip = () => {
    const { activeStep } = this.state;
    if (!this.isStepOptional(activeStep)) {
      // You probably want to guard against something like this,
      // it should never occur unless someone's actively trying to break something.
      throw new Error("You can't skip a step that isn't optional.");
    }
    const skipped = new Set(this.state.skipped.values());
    skipped.add(activeStep);
    this.setState({
      activeStep: this.state.activeStep + 1,
      skipped,
    });
  };

  handleReset = () => {
    this.setState({
      activeStep: 0,
    });
  };

  render() {
      var self = this;
    const { classes } = this.props;
    const steps = getSteps();
    const { activeStep } = this.state;

    return (
      <div className={classes.root}>
        <Stepper activeStep={activeStep}>
          {steps.map((label, index) => {
            const props = {};
            const labelProps = {};
            if (this.isStepOptional(index)) {
              labelProps.optional = <Typography variant="caption">Optional</Typography>;
            }
            if (this.isStepSkipped(index)) {
              props.completed = false;
            }
            return (
              <Step key={label} {...props}>
                <StepLabel {...labelProps}>{label}</StepLabel>
              </Step>
            );
          })}
        </Stepper>
        <div>
          {activeStep === steps.length ? (
            <div>
              <Typography className={classes.instructions}>
                All steps completed - you&quot;re finished
              </Typography>
              <Button onClick={this.handleReset} className={classes.button}>
                Reset
              </Button>
            </div>
          ) : (
            <div>
              <Typography className={classes.instructions}>{self.getStepContent(activeStep)}</Typography>
              <div>
                <Button
                  disabled={activeStep === 0}
                  onClick={this.handleBack}
                  className={classes.button}
                >
                  Back
                </Button>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={this.handleNext}
                  className={classes.button}
                >
                  {activeStep === steps.length - 1 ? 'Show Dispersion' : 'Next'}
                </Button>
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }
}

export default withStyles(styles)(Steps);
