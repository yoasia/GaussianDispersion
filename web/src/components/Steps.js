import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import EventEmitter from 'event-emitter';


var emitter;

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
  return ['Select location', 'Create an ad group', 'Create an ad'];
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
            data:{
                lon:null, 
                lat:null,
                windSpeed:null,
                windDirecrion:null,
                height:null,
                sourceStrength:null,
                refflectionCo:null,
                weatherStabilityClass:null,
                areaDimension:null,
                grid:null
            }
          };
         
        this.lonFieldVal = null;
        this.latFieldVal = null;
        emitter = this.props.emitter;

        this.getStepContent = this.getStepContent.bind(this);
        this.setLonLat = this.setLonLat.bind(this);
        this.setLon = this.setLon.bind(this);
        this.setLat = this.setLat.bind(this);
    }

    
    static propTypes = {
        classes: PropTypes.object,
    };
    
    componentWillMount() {
        emitter.on('lonLatChanged', this.setLonLat);
    }
    
    /**
     * 
     * @param {number} lon 
     * @param {number} lat 
     */
    setLonLat(lon, lat){
        var data = this.state.data;

        [this.lonFieldVal, this.latFieldVal] = [lon, lat];
        [data.lon, data.lat] = [lon, lat];

        this.setState({data});
    }

    /**
     * 
     * @param {Object} event 
     */
    setLon(event){
        this.lonFieldVal = event.target.value;
        
        if(!isNaN(this.lonFieldVal)){
            var data = this.state.data;
            data.lon = this.lonFieldVal;
            emitter.emit("lonLatChanged", data.lon, data.lat);
        }
    }
    /**
     * 
     * @param {Object} event 
     */
    setLat(event){
        this.latFieldVal = event.target.value;
        
        if(!isNaN(this.latFieldVal)){
            var data = this.state.data;
            data.lat = this.latFieldVal;
            emitter.emit("lonLatChanged", data.lon, data.lat);
        }
    }

  getStepContent(step) {
    const { classes } = this.props;
    switch (step) {
      case 0:
        return (<form className={classes.container} noValidate autoComplete="off">
        <TextField
          id="lonTextField"
          label="Longitude"
          defaultValue="20"
          value={this.lonFieldVal}
          className={classes.textField}
          onChange={this.setLon}
          margin="normal"
          error={(isNaN(this.lonFieldVal)? true:false )}
        />
        <TextField
          id="latTextField"
          label="Latitude"
          defaultValue="52"
          value={this.latFieldVal}
          onChange={this.setLat}
          className={classes.textField}
          margin="normal"
          error={(isNaN(this.latFieldVal)? true:false )}
        />
      </form>);
      case 1:
        return 'What is an ad group anyways?';
      case 2:
        return 'This is the bit I really care about!';
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
    emitEventDraggableMarker(activeStep);
    let { skipped } = this.state;

    if (this.isStepSkipped(activeStep)) {
      skipped = new Set(skipped.values());
      skipped.delete(activeStep);
    }
    this.setState({
      activeStep: activeStep + 1,
      skipped,
    });
  };

  handleBack = () => {
    const { activeStep } = this.state;
    emitEventDraggableMarker(activeStep);

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
                {this.isStepOptional(activeStep) && (
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={this.handleSkip}
                    className={classes.button}
                  >
                    Skip
                  </Button>
                )}
                <Button
                  variant="contained"
                  color="primary"
                  onClick={this.handleNext}
                  className={classes.button}
                >
                  {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
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
