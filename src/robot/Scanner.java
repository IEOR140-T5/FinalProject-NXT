package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class Scanner {

	private LightSensor lightSensor;
	private UltrasonicSensor ultraSensor;
	private NXTRegulatedMotor motor;
	private int[] bearings;

	public Scanner(NXTRegulatedMotor m, LightSensor ls, UltrasonicSensor us) {
		motor = m;
		lightSensor = ls;
		ultraSensor = us;
		lightSensor.setFloodlight(false);
		bearings = new int[2];
		m.setSpeed(70);
	}


	/**
	 * Scans for beacons between a specified start angle and end angle, and then
	 * backwards again. It looks only at light values above 42, which seems to be
	 * a good threshold to use, and stores the peaks found in the scanner's bearings
	 * instance variable.
	 * 
	 * @param startAngle angle to start the scan at, in degrees from forward
	 * @param endAngle   angle to end 1 scan at, in degrees from forward.
	 */
	public void lightScan(int startAngle, int endAngle) {
		int threshold = 35;

		motor.rotateTo(startAngle);

		int[] ccwBearings = {1000, 1000};
		int[] cwBearings = {1000, 1000};

		int highestLightValue = 0;
		int ccwIndex = 0;
		int cwIndex = 0;
		boolean ccw = false;
		boolean ccwAssigned = false;
		boolean cwAssigned = false;

		int[] startAngles = {startAngle, endAngle};
		int[] endAngles = {endAngle, startAngle};

		for (int i = 0; i < 2; i++) {
			ccw = (endAngles[i] > startAngles[i]);

			motor.rotateTo(endAngles[i], true);

			while (motor.isMoving()) {
				int newAngle = motor.getTachoCount();

				int lv = lightSensor.getLightValue();

				if ((lv > threshold) && (lv > highestLightValue)) {
					highestLightValue = lv;
					if (ccw) {
						ccwBearings[ccwIndex] = newAngle;
						ccwAssigned = true;
					} else if (!ccw) {
						cwBearings[cwIndex] = newAngle;
						cwAssigned = true;
					}
				} else if ((lv < threshold) && (ccw && ccwAssigned && ccwIndex == 0)) {
					ccwIndex++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 200, 5);
				} else if ((lv < threshold) && (!ccw && cwAssigned && cwIndex == 0)) {
					cwIndex++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 700, 5);
				}

			}

			highestLightValue = 0;
		}

		calculateBearingsFromLightValues(ccwBearings, cwBearings);
	}


	/**
	 * Takes both sets of bearings found from the clockwise scan and the counterclockwise
	 * scans, then combines them to create a set of true bearings to the beacons.
	 * 
	 * @param ccwBearings the bearings gotten from the counterclockwise scan
	 * @param cwBearings  the bearings gotten from the clockwise scan
	 */
	public void calculateBearingsFromLightValues(int[] ccwBearings, int[] cwBearings) {
		if (ccwBearings[1] == 1000) {

			if (Math.abs(ccwBearings[0] - cwBearings[0]) <= 15) {
				ccwBearings[1] = ccwBearings[0];
				ccwBearings[0] = 1000;
			}

		} else if (cwBearings[1] == 1000) {

			if (Math.abs(cwBearings[0] - ccwBearings[0]) <= 15) {
				cwBearings[1] = cwBearings[0];
				cwBearings[0] = 1000;
			}

		}

		for (int i = 0; i < ccwBearings.length; i++) {
			if (ccwBearings[i] == 1000) {
				ccwBearings[i] = cwBearings[1 - i];
			} else if (cwBearings[i] == 1000) {
				cwBearings[i] = ccwBearings[1 - i];
			}

			bearings[i] = (ccwBearings[i] + cwBearings[1 - i]) / 2;
		}
	}

	/**
	 * Returns the echo distance to something at a specific angle.
	 * 
	 * @param angle the angle the head should go to
	 * @return the echo distance at that angle
	 */
	public int getEchoDistance(float angle) {
		//Given angle from heading to wall, get the distance to that wall

		while (Math.abs(angle - motor.getTachoCount()) > 180) {
			if (angle > motor.getTachoCount()) {
				angle -= 360;
			} else {
				angle += 360;
			}
		}

		motor.rotateTo((int) angle);

		return ultraSensor.getDistance();
	}

	/**
	 * Returns the echo distance to something at a specific angle.
	 * 
	 * @param angle the angle the head should go to
	 * @return the echo distance at that angle
	 */
	public int getDistance(float angle) {
		//Given angle from heading to wall, get the distance to that wall

		while (Math.abs(angle - motor.getTachoCount()) > 180) {
			if (angle > motor.getTachoCount()) {
				angle -= 360;
			} else {
				angle += 360;
			}
		}

		motor.rotateTo((int) angle);

		return ultraSensor.getDistance();
	}
	/** 
	 * Returns the echo distance at the angle the scanner head is currently facing
	 * @return the echo distance
	 */
	public int getEchoDistance() {
		return ultraSensor.getDistance();
	}

	/** 
	 * Returns the echo distance at the angle the scanner head is currently facing
	 * @return the echo distance
	 */
	public int getDistance() {
		return ultraSensor.getDistance();
	}

	/**
	 * Rotates the scanner head to a specific angle.
	 * 
	 * @param angle the angle the head to rotate to
	 */
	public void rotateHeadTo(float angle) {
		if (Math.abs(angle - motor.getTachoCount()) > 180) {
			if (angle > motor.getTachoCount()) {
				angle -= 360;
			} else {
				angle += 360;
			}
		}

		motor.rotateTo((int) angle);
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
	
	public void rotate(float angle, boolean what){
		motor.rotate((int) angle, what);
	}

	public NXTRegulatedMotor getMotor(){
		return motor;
	}
}

