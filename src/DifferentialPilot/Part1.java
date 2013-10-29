package DifferentialPilot;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.Motor;
import lejos.nxt.Button;

/**
 * This class can trace a square of any size and in any direction. Uses Differential Pilot
 * @author Khoa Tran, Trevor Davenport, Phuoc nguyen
 */
public class Part1 {
	/**
	 * Instance variables
	 */
	DifferentialPilot pilot;
	private static int SQUAREANGLE = 90;
	private static float wheelDiameter =  5.38f;
	private static float trackWidth = 10.8f;
	
	/**
	 * The constructor for this class.
	 * you need to create a Pilot first,  then pass it here
	 */
	public Part1(DifferentialPilot aPilot) {
		pilot = aPilot;
		pilot.setTravelSpeed(30);
		pilot.setAcceleration(119);
		pilot.setRotateSpeed(270); 
	}

	/**
	 * Constructs the robot and drives it to meet the project specifications
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.out.println("GO");
		Button.waitForAnyPress();
		DifferentialPilot aPilot = new DifferentialPilot(wheelDiameter, trackWidth, Motor.A, Motor.C);
		Part1 robot = new Part1(aPilot);

		int length = 488;
		int direction = 1;
		
		aPilot.travel(length);
		
		
		/**
		for (int i = 0; i < 4; i++) {
			robot.square(length, direction);
			if (i == 1) { // switch to the other direction after the second iteration
				robot.pilot.rotate(SQUAREANGLE);
				direction = -1;
			}
		}
		**/
	}
	
	/**
	 * Top level task:  trace a square of specified size, and direction
	 * @param length
	 * @param direction 
	 */
	public void square(float length, int direction) {
		for (int i = 0; i < 4; i++) {
			pilot.travel(length);
			pilot.rotate(SQUAREANGLE  * direction);
		}
	}
}
