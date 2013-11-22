package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

/**
 * Scanner takes care of the Light Value for the bearings and Ultrasonic value, or the Echo Distance to the wall
 * or objects.
 * @author Phuoc Nguyen, Khoa Tran
 *
 */
public class Scanner {

	private LightSensor lightSensor;
	private UltrasonicSensor ultraSensor;
	private NXTRegulatedMotor motor;
	private int[] beaconBearings;
	private int _max = 1000;
	private int _THREADHOLD = 35;

	public Scanner(NXTRegulatedMotor mtr, LightSensor lsen, UltrasonicSensor usen) {
		motor = mtr;
		lightSensor = lsen;
		ultraSensor = usen;
		lightSensor.setFloodlight(false);
		beaconBearings = new int[2];
		//motor.setSpeed(70);
	}


	/**
	 * Scan from a given angle to another given angle to get the bearings to 2 beacons
	 * We will scan twice and get 2 value for each light, the average them
	 */
	public void lightScan(int startAngle, int endAngle) {
		motor.rotateTo(startAngle);

		int[] counterClockwiseBearings = {_max, _max};
		int[] clockwiseBearings = {_max, _max};
		// each array corresponds to each sweep
		int[] startAngles = {startAngle, endAngle};
		int[] endAngles = {endAngle, startAngle};
		
		int highestLightValue = 0, counterClockwise = 0, clockwise = 0;
		boolean isCounterClockwise = false, isAssignedToCCW = false, isAssignedToCW = false;

		// Scan twice for each beacon 
		for (int i = 0; i < 2; i++) {
			isCounterClockwise = (endAngles[i] > startAngles[i]);

			motor.rotateTo(endAngles[i], true);

			while (motor.isMoving()) {
				int newAngle = motor.getTachoCount();

				int currentLightValue = lightSensor.getLightValue();

				if ((currentLightValue > _THREADHOLD) && (currentLightValue > highestLightValue)) {
					highestLightValue = currentLightValue;
					
					if (isCounterClockwise) {
						counterClockwiseBearings[counterClockwise] = newAngle;
						isAssignedToCCW = true;
					}
					
					if (!isCounterClockwise) {
						clockwiseBearings[clockwise] = newAngle;
						isAssignedToCW = true;
					}
				// the the light value goes down
				} else if ((currentLightValue < _THREADHOLD) && (isCounterClockwise && isAssignedToCCW && counterClockwise == 0)) {
					counterClockwise++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 200, 5);
				} else if ((currentLightValue < _THREADHOLD) && (!isCounterClockwise && isAssignedToCW && clockwise == 0)) {
					clockwise++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 700, 5);
				}

			}
			// reset it
			highestLightValue = 0;
		}
		
		// Now average them 
		calculateBearings(counterClockwiseBearings, clockwiseBearings);
	}


	/**
	 * Calculate the average value for each of the beacon
	 * @param ccwBearings: counter clockwise bearings
	 * @param cwBearings: clockwise bearings
	 */
	public void calculateBearings(int[] counterClockwiseBearings, int[] clockwiseBearings) {
		// First check if we didn't get the value, then the value would be from the other sweep
		for (int i = 0; i < counterClockwiseBearings.length; i++) {
			if (counterClockwiseBearings[i] == _max) {
				counterClockwiseBearings[i] = clockwiseBearings[1 - i];
			} 
			if (clockwiseBearings[i] == _max) {
				clockwiseBearings[i] = counterClockwiseBearings[1 - i];
			}
			// take the average
			beaconBearings[i] = (counterClockwiseBearings[i] + clockwiseBearings[1 - i]) / 2;
		}
	}

	/**
	 * Return the distance to a specific angle
	 * @param angle: angle to return distance
	 * @return: echo distance
	 */
	public int getEchoDistance(float angle) {
		motor.rotateTo((int) angle);
		return ultraSensor.getDistance();
	}

	/** 
	 * Return the distance to the current heading
	 * @return : current distance
	 */
	public int getEchoDistance() {
		return ultraSensor.getDistance();
	}

	/**
	 * Rotate to a specific angle
	 */
	public void rotateHeadTo(float angle) {
		motor.rotateTo((int) angle);
	}

	/**
	 * get the current angle the the scanner is currently at
	 * @return angle
	 */
	public int getHeadAngle() {
		return motor.getTachoCount();
	}

	/**
	 * Get the beacon bearings
	 * @return the bearings array
	 */
	public int[] getBearings() {
		return beaconBearings;
	}

}

