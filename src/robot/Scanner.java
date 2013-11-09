package robot;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.TouchSensor;

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
	private int[] bearings;
	public static final int THRESHOLD = 35;
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
		motor.setSpeed(20);
		lightSensor = theEye;
		lightSensor.setFloodlight(false);
		bearings = new int[2];
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
		bearings = new int[2];
	}
	
	/**
	 * Scans lights from startAngle to endAngle
	 * @param startAngle - where to begin scanning
	 * @param endAngle - where to end scanning
	 */
	public void scanLights(int startAngle, int endAngle) {
		int[] counterBearings = {MAX, MAX};
		int[] clockBearings = {MAX, MAX};
		int highestLightValue = 0;
		int counterIndex = 0;
		int clockIndex = 0;
		boolean counterClockwise = false;
		boolean counterSet = false;
		boolean clockSet = false;

		int[] startAngles = {startAngle, endAngle};
		int[] endAngles = {endAngle, startAngle};
		
		motor.rotateTo(startAngle);
		for (int i = 0; i < 2; i++) {
			counterClockwise = endAngles[i] > startAngles[i];
			motor.rotateTo(endAngles[i], true);

			while (motor.isMoving()) {
				int newAngle = motor.getTachoCount();
				int lv = lightSensor.getLightValue();

				if ((lv > THRESHOLD) && (lv > highestLightValue)) {
					highestLightValue = lv;
					if (counterClockwise) {
						counterBearings[counterIndex] = newAngle;
						counterSet = true;
					} else if (!counterClockwise) {
						clockBearings[clockIndex] = newAngle;
						clockSet = true;
					}
					LCD.drawInt(0, 1, 1);
				} else if ((lv < THRESHOLD) && (counterClockwise && counterSet && counterIndex == 0)) {
					counterIndex++;
					highestLightValue = 0;
					playSound(true);
				} else if ((lv < THRESHOLD) && (!counterClockwise && clockSet && clockIndex == 0)) {
					clockIndex++;
					highestLightValue = 0;
					playSound(false);
				}
			}
			highestLightValue = 0;
		}
		calculateBearings(counterBearings, clockBearings);
	}

	/**
	 * Plays some sound according to the turn angle
	 */
	private void playSound(boolean increase) {
		if (increase) {
			for (int i = 0; i < 4; i++) {
				Sound.playNote(Sound.PIANO, 150*i, 5);
			}
		} else {
			for (int i = 4; i > 0; i--) {
				Sound.playNote(Sound.PIANO, 150*i, 5);
			}
		}
	}
	
	/**
	 * Calculates final bearings based on the bearings clockwise and counterclockwise
	 * @param counterBearings - counterclockwise bearings
	 * @param clockBearings - clockwise bearings
	 */
	private void calculateBearings(int[] counterBearings, int[] clockBearings) {
		for (int i = 0; i < counterBearings.length; i++) {
            bearings[i] = (counterBearings[i] + clockBearings[1 - i]) / 2;
		}
	}

	/**
	 * Returns the echo distance to the wall at a given angle
	 * @param angle the angle the head should rotate to
	 * @return the echo distance at that angle
	 */
	public int getDistanceToWall(float angle) {
		motor.rotateTo((int) angle);
		return ultraSensor.getDistance();
	}

	/**
	 * Gets the distance at the current heading
	 * @return the distance at the current heading
	 */
	public int getDistance(){
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
	public int[] getBearings() {
		return bearings;
	}
}
