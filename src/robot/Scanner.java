package robot;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;

/**
 * Scanner class which implements logic for scanning lights and obstacles
 * Adapted from ScanRecorder.java in the skeleton
 * 
 * @author Khoa Tran, Phuoc Nguyen
 */
public class Scanner {

	/**
	 * Instance variables
	 */
	private NXTRegulatedMotor motor;
	private LightSensor lightSensor;
	private UltrasonicSensor ultraSensor;

	private float[] bearings = new float[2];
	public static final int THRESHOLD = 30;
	public static final int MAX = 1000;

	/**
	 * Constructor for Scanner class that takes in the motor and lightsensor as
	 * params
	 * 
	 * @param theMotor
	 *            - motor of the robot
	 * @param theEye
	 *            - light sensor to detect the lights
	 */
	public Scanner(NXTRegulatedMotor theMotor, LightSensor theEye) {
		motor = theMotor;
		motor.setSpeed(30);
		lightSensor = theEye;
		lightSensor.setFloodlight(false);
	}

	/**
	 * Constructor for Scanner class that takes in the motor and lightsensor as
	 * params
	 * 
	 * @param theMotor
	 *            - motor of the robot
	 * @param theEye
	 *            - light sensor to detect the lights
	 */
	public Scanner(NXTRegulatedMotor theMotor, LightSensor theEye,
			UltrasonicSensor ussensor) {
		motor = theMotor;
		motor.setSpeed(20);
		lightSensor = theEye;
		lightSensor.setFloodlight(false);
		ultraSensor = ussensor;
	}

	/**
	 * scan both ways for 2 sets of angle, in order to calculate the mean of it
	 * 
	 * @param startAngle
	 *            - where to begin scanning
	 * @param endAngle
	 *            - where to end scanning
	 */
	public void scanLights(int startAngle, int endAngle) {
		int highestLightValue = 0;
		int[] clockwise = new int[2];
		int[] counterClockwise = new int[2];
		boolean isFirstLight = true;

		motor.rotateTo(startAngle);

		// sweeping counter clockwise
		motor.rotateTo(endAngle, true);
		while (motor.isMoving()) {
			int currentAngle = motor.getTachoCount();
			int currentLightValue = lightSensor.getLightValue();

			if ((currentLightValue > THRESHOLD)
					&& (currentLightValue > highestLightValue)) {
				highestLightValue = currentLightValue;
				if (isFirstLight) {
					counterClockwise[0] = currentAngle;
					Sound.playNote(Sound.PIANO, 500, 5);
					isFirstLight = false;
				} else {
					counterClockwise[1] = currentAngle;
					Sound.playNote(Sound.PIANO, 150, 5);
					isFirstLight = true;
				}
			}
			highestLightValue = 0;
		}

		// sweeping clockwise
		motor.rotateTo(endAngle, true);
		while (motor.isMoving()) {
			int currentAngle = motor.getTachoCount();
			int currentLightValue = lightSensor.getLightValue();

			if ((currentLightValue > THRESHOLD)
					&& (currentLightValue > highestLightValue)) {
				highestLightValue = currentLightValue;
				if (isFirstLight) {
					clockwise[0] = currentAngle;
					Sound.playNote(Sound.PIANO, 150, 5);
					isFirstLight = false;
				} else {
					clockwise[1] = currentAngle;
					Sound.playNote(Sound.PIANO, 500, 5);
					isFirstLight = true;
				}
			}
			highestLightValue = 0;
		}

		// calculate the mean of 2 scanning and save it to the private members
		calculateBearings(counterClockwise, clockwise);
	}

	/**
	 * Calculates final bearings based on the bearings clockwise and
	 * counterclockwise
	 * 
	 * @param counterClockwiseBearings
	 *            - counterclockwise bearings
	 * @param clockwiseBearings
	 *            - clockwise bearings
	 */
	private void calculateBearings(int[] counterClockwiseBearings,
			int[] clockwiseBearings) {
		for (int i = 0; i < counterClockwiseBearings.length; i++) {
			bearings[i] = (counterClockwiseBearings[i] + clockwiseBearings[1 - i]) / 2;
		}
	}

	/**
	 * Returns the echo distance to the wall at a given angle
	 * 
	 * @param angle
	 *            the angle the head should rotate to
	 * @return the echo distance at that angle
	 */
	public int getDistanceToWall(float angle) {
		motor.rotateTo((int) angle);
		Sound.playNote(Sound.PIANO, 1000, 5);
		return ultraSensor.getDistance();
	}

	/**
	 * Gets the distance at the current heading
	 * 
	 * @return the distance at the current heading
	 */
	public int getDistance() {
		return ultraSensor.getDistance();
	}

	/**
	 * Rotates the scanner head to the angle
	 * 
	 * @param angle
	 *            - how much to rotate to
	 * @param instantReturn
	 *            - if true, we don't wait until the whole rotate process
	 *            completes
	 */
	public void rotateTo(int angle, boolean instantReturn) {
		motor.rotateTo(angle, instantReturn);
	}

	/**
	 * @return the ultrasonic sensor
	 */
	protected UltrasonicSensor getUltrasonicSensor() {
		return ultraSensor;
	}

	/**
	 * @return the angle that the head is currently looking in.
	 */
	public int getHeadAngle() {
		return motor.getTachoCount();
	}

	/**
	 * @return the relative bearings to the light beacons stored in scanner
	 */
	public float[] getBearings() {
		return bearings;
	}
}
