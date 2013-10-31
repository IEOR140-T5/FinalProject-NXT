package robot;

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
		motor.setSpeed(500);
		motor.setAcceleration(4000);
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
		motor.setSpeed(300);
		lightSensor = theEye;
		lightSensor.setFloodlight(false);
		ultraSensor = ussensor;
	}
	
	public void scanLights(int startAngle, int endAngle) {
		int[] counterBearings = {MAX, MAX};
		int[] clockBearings = {MAX, MAX};
		int highestLightValue = 0;
		int ccwIndex = 0;
		int cwIndex = 0;
		boolean ccw = false;
		boolean ccwAssigned = false;
		boolean cwAssigned = false;

		int[] startAngles = {startAngle, endAngle};
		int[] endAngles = {endAngle, startAngle};
		
		motor.rotateTo(startAngle);
		for (int i = 0; i < 2; i++) {
			ccw = (endAngles[i] > startAngles[i]);
			motor.rotateTo(endAngles[i], true);

			while (motor.isMoving()) {
				int newAngle = motor.getTachoCount();

				int lv = lightSensor.getLightValue();

				if ((lv > THRESHOLD) && (lv > highestLightValue)) {
					highestLightValue = lv;
					if (ccw) {
						counterBearings[ccwIndex] = newAngle;
						ccwAssigned = true;
					} else if (!ccw) {
						clockBearings[cwIndex] = newAngle;
						cwAssigned = true;
					}
				} else if ((lv < THRESHOLD) && (ccw && ccwAssigned && ccwIndex == 0)) {
					ccwIndex++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 200, 5);
					Sound.playNote(Sound.PIANO, 300, 5);
					Sound.playNote(Sound.PIANO, 400, 5);
					Sound.playNote(Sound.PIANO, 700, 5);
				} else if ((lv < THRESHOLD) && (!ccw && cwAssigned && cwIndex == 0)) {
					cwIndex++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 700, 5);
					Sound.playNote(Sound.PIANO, 400, 5);
					Sound.playNote(Sound.PIANO, 300, 5);
					Sound.playNote(Sound.PIANO, 200, 5);
				}
			}
			highestLightValue = 0;
		}
		calculateBearings(counterBearings, clockBearings);
	}

	/**
	 * 
	 * @param counterBearings
	 * @param clockBearings
	 */
	private void calculateBearings(int[] counterBearings, int[] clockBearings) {
		for (int i = 0; i < counterBearings.length; i++) {
            bearings[i] = (counterBearings[i] + clockBearings[1 - i]) / 2;
		}
	}

	/**
	 * Gets the distance at the current headinh
	 * @return
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
	 * 
	 * @return the angle that the head is currently looking in.
	 */
	public int getHeadAngle() {
		return motor.getTachoCount();
	}

	/**
	 * 
	 * @return the relative bearings to the light beacons stored in scanner
	 */
	public int[] getBearings() {
		return bearings;
	}
}
