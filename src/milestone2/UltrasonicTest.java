package milestone2;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Datalogger;

public class UltrasonicTest {

	// Instance variables
	private UltrasonicSensor us;
	private NXTRegulatedMotor motor;
	private Datalogger dl;

	/**
	 * Scans -90 to 90
	 */
	public void scan() {
		motor.rotateTo(-90, false);
		dl.writeLog(us.getDistance());
		motor.rotateTo(90, false);
		dl.writeLog(us.getDistance());
	}

	/**
	 * Constructor for UltrasonicTest
	 */
	public UltrasonicTest() {
		us = new UltrasonicSensor(SensorPort.S3);
		motor = Motor.B;
		dl = new Datalogger();
	}

	/**
	 * Rotates to a certain angle
	 * 
	 * @param angle
	 */
	public void rotateTo(int angle) {
		motor.rotateTo(angle);
	}

	/**
	 * Test code for ultrasonic test - prelim 2
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		UltrasonicTest usr = new UltrasonicTest();
		for (int i = 0; i < 3; i++) {
			Button.waitForAnyPress();
			usr.scan();
		}
		usr.rotateTo(0);

		Button.waitForAnyPress();
		usr.dl.transmit();
	}

}
