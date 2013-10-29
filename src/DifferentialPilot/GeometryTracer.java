package DifferentialPilot;
import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Traces different geometrical shapes, including polygon and circle's arc
 * @author Khoa Tran, Phuoc Nguyen
 */

public class GeometryTracer {
	/**
	 * Instance variables
	 */
	private DifferentialPilot pilot;
	
	/**
	 * Constructor for GeometryTracer
	 * @param pilot - the differential pilot used to trace the geometrical shapes
	 */
	public GeometryTracer(DifferentialPilot pilot) {
		this.pilot = pilot;
		pilot.setTravelSpeed(30);
		pilot.setAcceleration(90);
		pilot.setRotateSpeed(180);
	}
	/**
	 * trace an arc of a circle
	 * @param radius of the arc
	 * @param angle of rotation
	 * @param turnDirection - the direction to turn, right is 1, left is -1 
	 */
	public void arc(float radius,  float  angle, int turnDirection) {
		LCD.drawString("Circle Arc", 0, 0);
		Button.waitForAnyPress();
		pilot.arc(radius * turnDirection, angle);
	}

	/**
	 * trace a polygon
	 * @param length - of side
	 * @param sides - number of sides of the polygon
	 * @param turnDirection - the direction to turn, right is 1, left is -1 
	 */
	public void polygon(float length, int sides, int turnDirection) {
		LCD.drawString("Polygon", 0, 0);
		Button.waitForAnyPress();
		
		int angle = 360 / sides; 
		LCD.drawString("Angle: " + angle, 0, 0);
		for (int i = 0; i < sides; i++) { 
			pilot.travel(length);
			pilot.rotate(angle * turnDirection);
		}
	}
}