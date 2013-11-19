package robot;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;

public class VariancePose extends Pose {
    private float varX;
    private float varY;
    private float varH;

    public VariancePose() {
        super();
        varX = 0;
        varY = 0;
        varH = 0;
    }

    public VariancePose(float x, float y, float h, float varX, float varY, float varH) {
        _location = new Point(x,y);
        _heading = h;
        this.varX = varX;
        this.varY = varY;
        this.varH = varH;
    }

    @Override
    public String toString() {
        String location = "X:" + _location.x + " Y:" + _location.y + " H:" + _heading;
        String variance = "varX:" + varX + " varY:" + varY + " varH:" + varH;
        return location + "\n" + variance;
    }
    
    public float getVarX() {
    	return varX;
    }
    
    public float getVarY() {
    	return varY;
    }
    
    public float getVarH() {
    	return varH;
    }

}
