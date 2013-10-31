package milestone1;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.Motor;
import lejos.nxt.Button;

/**
 * This class can trace a square of any size and in any direction. Uses
 * Differential Pilot
 * 
 * @author Khoa Tran, Phuoc Nguyen
 */
public class Tracer {
	/**
	 * Instance and static variables for the Tracer object
	 */
	DifferentialPilot pilot;
	private static int squareAngle = 90;
	private static float leftWheelDiameter = 5.42f;
	private static float rightWheelDiameter = 5.44f;
	private static float trackWidth = 13.72f;

	/**
	 * Constructor for a geometry tracer robot
	 * 
	 * @param aPilot
	 *            - DifferentialPilot object
	 */
	public Tracer(DifferentialPilot aPilot) {
		pilot = aPilot;
		pilot.setTravelSpeed(30);
		pilot.setAcceleration(120);
		pilot.setRotateSpeed(270);
	}

	/**
	 * Constructs the robot and drives it to meet the project specifications
	 * 
	 * @param args
	 *            - command line arguments
	 */
	public static void main(String[] args) {
		// Create a DifferentialPilot and a Tracer objects
		DifferentialPilot aPilot = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);
		Tracer robot = new Tracer(aPilot);
		int lengthTrack = 488;
		int lengthSquare = 120;

		
		// Part I - trace 16 tiles
		System.out.println("Part I");
		Button.waitForAnyPress();
		Delay.msDelay(500);
		robot.pilot.travel(lengthTrack);

		// Part IIa - rotate 4 times
		System.out.println("Part IIa");
		Button.waitForAnyPress();
		Delay.msDelay(500);
		for (int i = 0; i < 4; i++) {
			robot.pilot.rotate(squareAngle);
			Delay.msDelay(100);
		}

		// Part IIb - rotate 4 times
		System.out.println("Part IIb");
		Button.waitForAnyPress();
		Delay.msDelay(500);
		for (int i = 0; i < 4; i++) {
			robot.pilot.rotate(squareAngle * -1);
			Delay.msDelay(150);
		}


		// Part III - trace a square of lengthSquare
		System.out.println("Part III");
		Button.waitForAnyPress();
		Delay.msDelay(500);
		robot.square(lengthSquare, 1);
		
		robot.pilot.rotate(squareAngle);
		Delay.msDelay(500);
		robot.square(lengthSquare, -1);
	}

	/**
	 * Traces a square of specified length
	 * 
	 * @param length
	 *            - length of one side of the square
	 */
	public void square(float length, int direction) {
		for (int i = 0; i < 4; i++) {
			pilot.travel(length);
			pilot.rotate(squareAngle * direction);
			Delay.msDelay(500);
		}
	}
}
