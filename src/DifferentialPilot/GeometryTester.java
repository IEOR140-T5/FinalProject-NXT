package DifferentialPilot;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.Button;
import lejos.nxt.Motor;

/**
 * NB rename this class so that it has a unique name on the Robot
 * @author Khoa Tran, Phuoc Nguyen, Corey Short, Trevor Davenport

 * @version 1.0
 */
public class GeometryTester {
	/**
	 * Constructs a the robot and drives it through the project specifications
	 * @param aArg 
	 */
	public static void main(String[] aArg) {
		float wheelDiameter =  5.38f;
		float trackWidth = 11.2f;
		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, Motor.A, Motor.C);
		GeometryTracer robot = new GeometryTracer(pilot);   
		
		// Trace a triangle with side length of 24 inches , making turns to the left
		robot.polygon(60.96f, 3, -1);
		
		// Trace a pentagon with the same length, but making turns to the right
		robot.polygon(60.96f, 5, 1);
		
		// Trace a semi-circle, turning to the left with radius 90cm
		robot.arc(90f, -180, -1);
		
		// Trace a quarter circle, turning right with a radius of 30cm
		robot.arc(30f, 90, 1);
	}  
}