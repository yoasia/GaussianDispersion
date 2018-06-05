/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Joanna
 */
public class StabilityY {
    private double a;
    private double b;
    private STABILITY_CLASS sClass;

    
    public StabilityY(STABILITY_CLASS _class ) {
        this.sClass = _class;
        switch (_class){
            case A:
                this.a = 0.28;
                this.b = 0.9;
                break;
            case B:
                this.a = 0.23;
                this.b = 0.85;
                break;
            case C:
                this.a = 0.22;
                this.b = 0.8;
                break;
            case D:
                this.a = 0.2;
                this.b = 0.76;
                break;
            case E:
                this.a = 0.15;
                this.b = 0.73;
                break;
            case F:
                this.a = 0.12;
                this.b = 0.67;
                break;
        }
    }
}
