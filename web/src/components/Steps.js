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
import LinearProgress from '@material-ui/core/LinearProgress';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Slider, { Range } from 'rc-slider';
import Tooltip from 'rc-tooltip';
import SquareIcon from '@material-ui/icons/CropSquare';
import PanoramaFishEye from '@material-ui/icons/PanoramaFishEye';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControl from '@material-ui/core/FormControl';
import {figureEnum, DEFAULT_ALPHA} from '../constants/visualization';

import 'rc-slider/assets/index.css';
import 'rc-tooltip/assets/bootstrap.css';

const Handle = Slider.Handle;
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

const wrapperStyle = { 
  width: 200, 
  margin: "auto",
  marginTop: "1em",
  marginBottom: "1em"
};
const buttonFigureStyle = { 
  margin: '2em auto',
  display: 'flex',
  justifyContent: 'center',
};

const styles = theme => ({
  root: {
    position: 'absolute',
    zIndex: '2',
    backgroundColor: 'white',
    width: '20%',
    height: '55%', 
    padding:'10px'
  },
  container: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'center',
    marginBottom: '4em'
  },
  buttonContainer:{
    bottom: '0px',
    position:'absolute',
    margin: theme.spacing.unit* 4
  },
  button: {
    marginRight: theme.spacing.unit,
  },
  buttonFigure:{
    marginRight: theme.spacing.unit,
    width: '10em'
  },
  instructions: {
    marginTop: theme.spacing.unit,
    marginBottom: theme.spacing.unit,
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200,
  },
  loader:{
    flexGrow:1,
  },
  leftIcon: {
    marginRight: theme.spacing.unit,
  },
  rightIcon: {
    marginLeft: theme.spacing.unit,
  },
  iconSmall: {
    fontSize: 20,
  },
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
function emitEventReset(step){
    emitter.emit("reset");
  }



class Steps extends React.Component {

    constructor(props) {
        super(props);
        
        this.state = {
            activeStep: 0,
            skipped: new Set(),
            downloaded:false,
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
                grid:50
            },
            displayParameters:{
              min_value:0,
              point_shape: figureEnum.CUBE,
              transparency: DEFAULT_ALPHA
            },
            max_value: null,
            checkedA:true
          };
        this.textFieldValues = Object.assign({}, this.state.parameters);
        this.displayFormValues = Object.assign({}, this.state.displayParameters);

        emitter = this.props.emitter;

        this.getStepContent = this.getStepContent.bind(this);
        this.setLonLat = this.setLonLat.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleChangeDisplay = this.handleChangeDisplay.bind(this);
        this.onFinish = this.onFinish.bind(this);

    }

    
    static propTypes = {
        classes: PropTypes.object,
    };
    
    componentWillMount() {
        emitter.on('lonLatChanged', this.setLonLat);
    }



    onFinish(){
      var self = this;
      this.props.onFinish(this.state.parameters);
      emitter.on('dataDownloaded', (max_value)=>{
          self.setState({downloaded: true, max_value });
      });
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
                parameters[name] = parseFloat(event.target.value);
                emitter.emit("lonLatChanged", parameters.lon, parameters.lat);
            } else{
                this.setState({parameters});
            }
    
        }
      };

    handleChangeDisplay = (name, value) => event => {
        value = value || event.value;
        this.displayFormValues[name] = value;
        var parameters = this.state.displayParameters;
        parameters[name] = value;

        this.setState({displayParameters:parameters}); 

        if(name == "point_shape"){
          emitter.emit("pointShapeChanged", value);

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
    emitEventDraggableMarker(this.state.activeStep + 1);
  };    

  handleReset = () => {
    this.setState({
      activeStep: 0,
      max_value: null
    });
    emitEventDraggableMarker(0);
    emitEventReset();
  };

  handleSlider = (props) => {
    const { value, dragging, index, ...restProps } = props;
    return (
      <Tooltip
        prefixCls="rc-slider-tooltip"
        overlay={value}
        visible={dragging}
        placement="top"
        key={index}
      >
        <Handle value={value} {...restProps} />
      </Tooltip>
    );
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
              <div className={classes.container}>
                <div className={classes.loader}>
                {
                  (this.state.downloaded == true) ? 
                  (
                  <div style={{marginTop:"2em", marginBottom:"1em"}}>
                      <Typography align='center' variant="title" color="inherit">
                        Display options
                      </Typography>
                      <div style={buttonFigureStyle}>
                          <Button 
                          variant={(this.state.displayParameters.point_shape == figureEnum.CUBE ? "contained" : "outlined")} 
                          color={(this.state.displayParameters.point_shape == figureEnum.CUBE ? "primary" : "default")} 
                          className={classes.buttonFigure}
                          value={figureEnum.CUBE}
                          onClick={this.handleChangeDisplay('point_shape', figureEnum.CUBE)}
                          >
                            Cubes
                            <SquareIcon className={classes.rightIcon} />
                          </Button>
                          <Button  
                          variant={(this.state.displayParameters.point_shape == figureEnum.SPHERE ? "contained" : "outlined")}
                          color={(this.state.displayParameters.point_shape == figureEnum.SPHERE ? "primary" : "default")} 
                          className={classes.buttonFigure}
                          value={figureEnum.SPHERE}
                          onClick={this.handleChangeDisplay('point_shape', figureEnum.SPHERE)}
                          >
                            Spheres
                            <PanoramaFishEye className={classes.rightIcon} />
                          </Button>
                      </div>
                      <div style={wrapperStyle}>
                        <Typography id="label">Min value</Typography>
                        <Slider 
                        min={0} 
                        max={this.state.max_value} 
                        step={this.state.max_value/100}
                        defaultValue={0} 
                        onAfterChange={this.handleChangeDisplay('min_value')}
                        handle={this.handleSlider}
                        trackStyle={{backgroundColor:"#3f51b5"}}
                        handleStyle={{border:"solid 2px #3f51b5"}}
                        />
                      </div>
                      <div style={wrapperStyle}> 
                        <Typography id="label2">Trasparency</Typography>
                        <Slider 
                        min={0} 
                        step={0.01}
                        max={1}
                        defaultValue={1} 
                        onAfterChange={this.handleChangeDisplay('transparency')} 
                        trackStyle={{backgroundColor:"#3f51b5"}}
                        handleStyle={{border:"solid 2px #3f51b5"}}
                        />
                      </div>
                  </div>)
                  :
                  <LinearProgress color="secondary" />
                }
                </div>
              </div>
              <div className={classes.buttonContainer}>
                <Button onClick={this.handleReset} className={classes.button}>
                  Reset
                </Button>
              </div>
              </div>
          ) : (
            <div>
              <div className={classes.instructions}>{self.getStepContent(activeStep)}</div>
              <div className={classes.buttonContainer}>
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
