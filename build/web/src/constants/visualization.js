
import getColorFromGradient from 'gpotter-gradient';

export const figureEnum = Object.freeze({"CUBE":1, "SPHERE":2});
export const DEFAULT_ALPHA = 1;
export const gradient = {
    0: '#FF284F',
    25: '#E8882C',
    50: '#FFE92C',
    75: '#42E839',
    100: '#5DFCFF'
  };

  export function valueToColor(value, max_value){
    const color = getColorFromGradient(gradient, (value/max_value) * 100); // #882737
    
    var r, g, b;

    r = parseInt(color.substring(1, 3), 16);
    g = parseInt(color.substring(3, 5), 16);
    b = parseInt(color.substring(5), 16);
    
    return {r, g, b};
}