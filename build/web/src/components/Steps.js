import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
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
import {WEATHER_STABILITY} from '../constants/weather';
import {NO_VALUE, GAS, TIME} from '../constants/constants';

import {styles, sliderStyle, wrapperStyle, buttonFigureStyle} from '../styles/Steps-styles';
import 'rc-slider/assets/index.css';
import 'rc-tooltip/assets/bootstrap.css';

const Handle = Slider.Handle;
var emitter;

function getSteps() {
  return ['Gas', "Source", 'Weather', 'Other parametrs'];
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
                calculationArea:1000,
                outputH: 100,
                gas_type:0,
                time: 1

            },
            displayParameters:{
              min_value:0,
              point_shape: figureEnum.CUBE,
              transparency: DEFAULT_ALPHA,
              layers:0
            },
            rangesNumber:null,
            maxValue: null,
          };
        this.textFieldValues = Object.assign({}, this.state.parameters);
        this.displayFormValues = Object.assign({}, this.state.displayParameters);

        emitter = this.props.emitter;

        this.getStepContent = this.getStepContent.bind(this);
        this.setLonLat = this.setLonLat.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleChangeDisplay = this.handleChangeDisplay.bind(this);
        this.onFinish = this.onFinish.bind(this);
        this.onDownloadGeoJSON = this.onDownloadGeoJSON.bind(this);
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
      emitter.on('dataDownloaded', (maxValue, rangesNumber)=>{
          self.setState({downloaded: true, maxValue, rangesNumber});
      });
    }

    onDownloadGeoJSON(){
      var self = this;
      emitter.emit('onGeoJSONDownload');
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
            parameters[name] = event.target.value == "" ? NO_VALUE : event.target.value;

            if((name == "lon" || name == "lat" || name == "height") && event.target.value){
                parameters[name] = parseFloat(event.target.value);
                emitter.emit("lonLatChanged", parameters.lon, parameters.lat, parameters.height);
            } else if( name == 'windDirection'){
                if(parseFloat(event.target.value) > 360)
                  parameters[name] = (parseInt(event.target.value) % 360);
                else if(parseFloat(event.target.value) < 0){
                  parameters[name] = (parseInt(event.target.value) + 360 * Math.floor(1 + (-1) *parseInt(event.target.value)/360));
                }
                this.setState({parameters});
            } else{
                this.setState({parameters});
            }
        }
      };

    handleChangeDisplay = (name, value) => event => {
        value = value || event;
        this.displayFormValues[name] = value;
        var parameters = this.state.displayParameters;
        parameters[name] = value;
        
        this.setState({displayParameters:parameters}); 
        
        if(name == "point_shape"){
          emitter.emit("pointShapeChanged", value);
        }
        else if(name == "transparency"){
          emitter.emit("transparencyChanged", value);
        }
        else if(name == "min_value"){
          emitter.emit("minValueChanged", value);
        }
        else if(name == "layers"){
          emitter.emit("displayedLayerChanged", value);
        }
      };    


  getStepContent(step) {
    const { classes } = this.props;
    switch (step) {
      case 0:
        return (
        <form className={classes.container} noValidate autoComplete="off">
            <FormControl className={classes.formControl}>
              <InputLabel htmlFor="gas-type">Gas type</InputLabel>
                <Select
                id="gasTypeSelect"
                label="Gas type"
                value={this.textFieldValues.gas_type}
                className={classes.selectField}
                onChange={this.handleChange('gas_type')}
                margin="normal"
                inputProps={{
                  name: 'Gas type',
                  id: 'gas-type',
                }}
                >
                    {GAS.map((name, index) => (
                    <MenuItem
                      key={index}
                      value={index}
                      selected={
                          (this.state.parameters.gas_type === index)
                      }
                    >
                      {name}
                    </MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField
            id="srcStrTextField"
            label="Gas emmission"
            type="number"
            value={this.textFieldValues.sourceStrength}
            onChange={this.handleChange('sourceStrength')}
            className={classes.textField}
            margin="normal"
            error={(isNaN(this.textFieldValues.sourceStrength)? true:false )}
            InputProps={{
                endAdornment: <InputAdornment position="end">grams/seconds</InputAdornment>,
            }}
            />
            <FormControl className={classes.formControl}>
              <InputLabel htmlFor="gas-type">Release time</InputLabel>
              <Select
              id="timeTypeSelect"
              label="Release time"
              value={this.textFieldValues.gas_type}
              className={classes.selectField}
              onChange={this.handleChange('time')}
              margin="normal"
              inputProps={{
                name: 'Release time',
                id: 'time',
              }}
              >
                  {TIME.map((name, index) => (
                  <MenuItem
                    key={index}
                    value={index}
                    selected={
                        (this.state.parameters.time === index)
                    }
                  >
                    {name} h
                  </MenuItem>
              ))}
            </Select>
            </FormControl>
        </form>);
      case 1:
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
            type="number"
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
        </form>);
      case 2:
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
                type="number"
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
                {WEATHER_STABILITY.map(option => (
                    <MenuItem key={option.value} value={option.value}>
                    {option.label}
                    </MenuItem>
                ))}
                </TextField>
                
            </form>);
      case 3:
        return (
            <form className={classes.container} noValidate autoComplete="off">
                <FormControl className={classes.formControl}>
                    <InputLabel htmlFor="output-height">Calculation area</InputLabel>
                    <Select
                    id="calculationAreaTextField"
                    label="Calculation area dimension"
                    value={this.textFieldValues.calculationArea}
                    className={classes.selectField}
                    onChange={this.handleChange('calculationArea')}
                    margin="normal"
                    inputProps={{
                      name: 'Calculation area height',
                      id: 'calculation-area',
                    }}
                    >
                      <MenuItem value={500}>
                        0.5km
                      </MenuItem>
                      <MenuItem value={1000} selected>1km</MenuItem>
                      <MenuItem value={2000}>2km</MenuItem>
                      <MenuItem value={3000}>3km</MenuItem>
                      <MenuItem value={5000}>5km</MenuItem>
                      <MenuItem value={7000}>7km</MenuItem>
                      <MenuItem value={10000}>10km</MenuItem>
                    </Select>
                </FormControl>

                  <FormControl className={classes.formControl}>
                    <InputLabel htmlFor="output-height">Calculation area height</InputLabel>
                    <Select
                    className={classes.selectField}
                    value={this.textFieldValues.outputH}
                    onChange={this.handleChange('outputH')}
                    label="Calculation area height"
                    margin="normal"
                    inputProps={{
                      name: 'Calculation area height',
                      id: 'output-height',
                    }}
                    >
                        <MenuItem value={0}>
                          <em>On the ground</em>
                        </MenuItem>
                        <MenuItem value={100} selected>100m</MenuItem>
                        <MenuItem value={500}>0.5km</MenuItem>
                        <MenuItem value={1000}>1km</MenuItem>
                        <MenuItem value={2000}>2km</MenuItem>
                        <MenuItem value={5000}>5km</MenuItem>
                    </Select>
                  </FormControl>
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

    if(activeStep > 2){
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
      maxValue: null
    });
    emitEventDraggableMarker(0);
    emitEventReset();
  };

  handleSlider = (props) => {
    const { value, dragging, index, ...restProps } = props;
    console.log("Slider moved.");
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
    const rangeValue = this.state.maxValue/this.state.rangesNumber;
    var marks = {
      0:' 0 - 1 [µg]',
      1:'1 - '+ Math.round(rangeValue) + ' [µg]'
    };

    for (let index = 2; index < this.state.rangesNumber; index++) {
      marks[index] =  index * Math.round(rangeValue) + ' - ' + (1 + index) * Math.round(rangeValue) + ' [µg]';
    }

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
                  <div style={{ marginBottom:"1em"}}>                      
                      <div style={sliderStyle}> 
                        <Typography id="label3">Show concentration </Typography>
                        <Slider 
                        min={0} 
                        step={1}
                        max={this.state.rangesNumber-1}
                        marks={marks}
                        defaultValue={0} 
                        onAfterChange={this.handleChangeDisplay('layers')} 
                        minimumTrackStyle={{
                          backgroundColor:"rgba(255, 255, 255, 0.9)",
                        }}
                        handleStyle={{
                          border:"solid 2px #3f51b5",
                        }}
                        railStyle={{
                          backgroundImage: 'linear-gradient('+
                          'to bottom, '+
                          '#FF284F, '+
                          '#E8882C,'+ 
                          '#FFE92C, '+
                          '#42E839, '+
                          '#5DFCFF)'
                        }}
                        dotStyle={{border:"solid 2px #3f51b5"}}
                        dots
                        vertical
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
                  BACK
                </Button>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={this.onDownloadGeoJSON}
                  className={classes.button}
                >
                  Download GeoJSON
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
