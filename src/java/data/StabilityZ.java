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
public class StabilityZ {
    private double p;

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public STABILITY_CLASS getsClass() {
        return sClass;
    }

    public void setsClass(STABILITY_CLASS sClass) {
        this.sClass = sClass;
    }
    private double q;
    private STABILITY_CLASS sClass;

    
    public StabilityZ(STABILITY_CLASS _class ) {
        this.sClass = _class;
        switch (_class){
            case A:
                this.p = -0.27819;
                this.q = 0.865;
                break;
            case B:
                this.p = -0.43063;
                this.q = 0.866;
                break;
            case C:
                this.p = -0.67985;
                this.q = 0.897;
                break;
            case D:
                this.p = -0.89279;
                this.q = 0.905;
                break;
            case E:
                this.p = -1.00877;
                this.q = 0.905;
                break;
            case F:
                this.p = -1.00877;
                this.q = 0.905;
                break;
        }
    }
}
