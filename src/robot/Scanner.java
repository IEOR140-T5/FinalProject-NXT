package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

/**
 * Scanner will take care all of the scanning part, by either using
 * LightSensor or UltrasonicSensor. All the value will be used to detect
 * the obstacle, calculate the position and most of all to find the bomb.
 * @author Phuoc Nguyen, Khoa Tran
 *
 */
public class Scanner {

    // Instances 
    private LightSensor lightSensor;
    private UltrasonicSensor ultraSensor;
    private NXTRegulatedMotor motor;
    private int[] beaconBearings = new int[2];
    private int _max = 1000;
    private int[] sweepingCounterClockwise = {_max, _max};
    private int[] sweepingClockwise = {_max, _max};
    private int _THRESHOLD = 35;
    

    /**
     * Constructor for Scanner
     * @param motor
     * @param lightsensor
     * @param ultrasonicsensor
     */
    public Scanner(NXTRegulatedMotor inputMotor, LightSensor inputLightSensor, UltrasonicSensor inputUltrasonicSensor) {
        motor = inputMotor;
        lightSensor = inputLightSensor;
        ultraSensor = inputUltrasonicSensor;
        lightSensor.setFloodlight(false);
        motor.setSpeed(40);
    }

	/**
	 * Scan from a given angle to another given angle to get the bearings to 2 beacons
	 * We will scan twice and get 2 value for each light, the average them
	 */
	public void lightScan(int startAngle, int endAngle) {

		motor.rotateTo(startAngle);

		int highestLightValue = 0;
		int counterClockwisePosition = 0;
		int clockwisePosition = 0;
		boolean isCounterClockwise = false;
		boolean saveCounterClockwise = false;
		boolean saveClockwise = false;
		
		// flush out the last values
		resetSweepingArrays();

		int[] startAngles = {startAngle, endAngle};
		int[] endAngles = {endAngle, startAngle};

		for (int i = 0; i < 2; i++) {
			isCounterClockwise = (endAngles[i] > startAngles[i]);

			motor.rotateTo(endAngles[i], true);

			while (motor.isMoving()) {
				int newAngle = motor.getTachoCount();

				int currentLightValue = lightSensor.getLightValue();

				if ((currentLightValue > _THRESHOLD) && (currentLightValue > highestLightValue)) {
					highestLightValue = currentLightValue;
					if (isCounterClockwise) {
						sweepingCounterClockwise[counterClockwisePosition] = newAngle;
						saveCounterClockwise = true;
					} else if (!isCounterClockwise) {
						sweepingClockwise[clockwisePosition] = newAngle;
						saveClockwise = true;
					}
				} else if ((currentLightValue < _THRESHOLD) && (isCounterClockwise && saveCounterClockwise && counterClockwisePosition == 0)) {
					counterClockwisePosition++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 200, 5);
				} else if ((currentLightValue < _THRESHOLD) && (!isCounterClockwise && saveClockwise && clockwisePosition == 0)) {
					clockwisePosition++;
					highestLightValue = 0;
					Sound.playNote(Sound.PIANO, 700, 5);
				}

			}

			highestLightValue = 0;
		}

		calculateBearings();
	}
	
	/**
	 * Locate the bomb based on a given limit angle
	 * @param startAngle
	 * @param endAngle
	 * @return
	 */

	public float[] locateTheBomb(int startAngle, int endAngle) {
        int smallestDistance = _max;
        float result[] = new float[2];

        motor.rotateTo(startAngle);

        // flush out the last value
        resetSweepingArrays();

        int[] startAngles = {startAngle, endAngle};
        int[] endAngles = {endAngle, startAngle};

        boolean isCounterClockwise;

        for (int i = 0; i < 2; i++) {
                smallestDistance = _max;

                isCounterClockwise = (endAngles[i] > startAngles[i]);

                motor.rotateTo(endAngles[i], true);

                while (motor.isMoving()) {
                        int currentAngle = motor.getTachoCount();
                        int distanceToBomb = ultraSensor.getDistance();
                        
                        // smaller value
                        if (distanceToBomb < smallestDistance) {
                                smallestDistance = distanceToBomb;
                                if (isCounterClockwise) {
                                        sweepingCounterClockwise[0] = currentAngle;
                                } else {
                                        sweepingClockwise[0] = currentAngle;
                                }
                        }
                        // equal value
                        if (distanceToBomb == smallestDistance) {
                                if (isCounterClockwise) {
                                        sweepingCounterClockwise[1] = currentAngle;
                                } else {
                                        sweepingClockwise[1] = currentAngle;
                                }
                        }
                }
        }
      
        result[0] = (sweepingCounterClockwise[0] + sweepingCounterClockwise[1] + 
                                sweepingClockwise[0] + sweepingClockwise[1]) / 4;
        result[1] = smallestDistance;
        
        return result;
    }
	
	/**
	 * Reset the bearings array everytime we do a scan and calculation
	 */
	public void resetSweepingArrays(){
		for (int i=0; i < 2; i++){
			sweepingCounterClockwise[i] = _max;
			sweepingClockwise[i] = _max;
		}
	}


	/**
	 * Calculate the average value for each of the beacon
	 * @param ccwBearings: counter clockwise bearings
	 * @param cwBearings: clockwise bearings
	 */
	public void calculateBearings() {
		// Check if the scan missed a beacon
		filterBearings();
		for (int i = 0; i < sweepingCounterClockwise.length; i++) {
			if (sweepingCounterClockwise[i] == _max) {
				sweepingCounterClockwise[i] = sweepingClockwise[1 - i];
			} else if (sweepingClockwise[i] == _max) {
				sweepingClockwise[i] = sweepingCounterClockwise[1 - i];
			}

			beaconBearings[i] = (sweepingCounterClockwise[i] + sweepingClockwise[1 - i]) / 2;
		}
	}

	/**
	 * Check whether the scan process missed a beacon or not
	 */
	public void filterBearings(){
		if (sweepingCounterClockwise[1] == _max) {
			if (Math.abs(sweepingCounterClockwise[0] - sweepingClockwise[0]) <= 15) {
				sweepingCounterClockwise[1] = sweepingCounterClockwise[0];
				sweepingCounterClockwise[0] = _max;
			}

		} else if (sweepingClockwise[1] == _max) {
			if (Math.abs(sweepingClockwise[0] - sweepingCounterClockwise[0]) <= 15) {
				sweepingClockwise[1] = sweepingClockwise[0];
				sweepingClockwise[0] = _max;
			}

		}
	}
	/**
	 * Return the distance to a specific angle
	 * @param angle: angle to return distance
	 * @return: echo distance
	 */
	public int getEchoDistance(float angle) {
		angle = normalize(angle);
		motor.rotateTo((int) angle);
		return ultraSensor.getDistance();
	}

	/**
	 * return the distance to a specific angle
	 * @param angle
	 * @return distance 
	 */
	public int getDistance(float angle) {
		angle = normalize(angle);
		motor.rotateTo((int) angle);
		return ultraSensor.getDistance();
	}
	
	/**
	 * Normalize the given angle
	 * @param angle
	 * @return normalized angle
	 */
	public float normalize(float angle){
		while (Math.abs(angle - motor.getTachoCount()) > 180) {
			if (angle > motor.getTachoCount()) {
				return (angle - 360);
			} else {
				return (angle + 360);
			}
		}
		return angle;
	}
	
	/** 
	 * Return the distance to the current heading
	 * @return : current distance
	 */
	public int getEchoDistance() {
		return ultraSensor.getDistance();
	}

	/**
	 * get distance from the current angle that its heading
	 * @return distance
	 */
	public int getDistance() {
		return ultraSensor.getDistance();
	}

	/**
	 * Rotate the scanner to a specific angle
	 * @param angle
	 */
	public void rotateHeadTo(float angle) {
		angle = normalize(angle);
		motor.rotateTo((int) angle);
	}

	/**
	 * Get the current heading of the angle
	 * @return angle
	 */
	public int getHeadAngle() {
		return motor.getTachoCount();
	}

	/**
	 * Get the bearings to 2 beacons
	 * @return bearing array
	 */
	public int[] getBearings() {
		return beaconBearings;
	}
	
	/**
	 * Rotate to an angle
	 * @param angle to rotate
	 * @param what return true/false
	 */
	public void rotate(float angle, boolean what){
		motor.rotate((int) angle, what);
	}

	/**
	 * get the motor to set speed
	 * @return motor
	 */
	public NXTRegulatedMotor getMotor(){
		return motor;
	}
	
	/**
	 * Rotate to a specific angle
	 * @param angle
	 * @param true or false?
	 */
	public void rotateTo(int angle, boolean needToReturn){
		motor.rotateTo(angle, needToReturn);
	}
}
