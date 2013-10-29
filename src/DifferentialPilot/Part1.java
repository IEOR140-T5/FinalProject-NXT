package DifferentialPilot;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
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
	private static int squareAngle = 90;
	private static float wheelDiameter =  5.38f;
	private static float trackWidth = 11.2f;
	private static float tuningParameter = 1.21f;
	
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
		DifferentialPilot aPilot = new DifferentialPilot(wheelDiameter, trackWidth, Motor.A, Motor.C);
		Part1 robot = new Part1(aPilot);
		int lengthTrack = 488;
		int lengthSquare = 120;
		
		System.out.println("Part I");
		Button.waitForAnyPress();
		aPilot.travel(lengthTrack);
		
		System.out.println("Part II");
		Button.waitForAnyPress();
		for(int i = 0; i < 4; i++) {
			aPilot.rotate(squareAngle * tuningParameter);
			Delay.msDelay(100);
		}
		
		System.out.println("Part III");
		Button.waitForAnyPress();
		robot.square(lengthSquare);
	}
	
	/**
	 * Top level task:  trace a square of specified size, and direction
	 * @param length
	 * @param direction 
	 */
	public void square(float length) {
		for (int i = 0; i < 4; i++) {
			pilot.travel(length);
			pilot.rotate(squareAngle  * tuningParameter);
		}
	}
}
