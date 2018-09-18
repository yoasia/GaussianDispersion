export const sliderStyle = {
  width: 200,
  height: 200,
  margin: "auto",
  marginTop: "1em",
  marginBottom: "1em",
  paddingBottom: 60
};

export const wrapperStyle = {
  width: 200,
  margin: "auto",
  marginTop: "1em",
  marginBottom: "1em"
};

export const buttonFigureStyle = {
  margin: '2em auto',
  display: 'flex',
  justifyContent: 'center',
};

export const styles = theme => ({
  root: {
    position: 'absolute',
    zIndex: '2',
    backgroundColor: 'white',
    width: 400,
    height: '55%',
    padding: '10px'
  },
  container: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'center',
    marginBottom: '4em'
  },
  buttonContainer: {
    bottom: '0px',
    position: 'absolute',
    margin: theme.spacing.unit * 4
  },
  button: {
    marginRight: theme.spacing.unit,
  },
  buttonFigure: {
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
  formControl:{
    marginTop: 2*theme.spacing.unit,
    marginBottom: theme.spacing.unit,
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200,
  },
  selectField:{

  },
  loader: {
    flexGrow: 1,
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
