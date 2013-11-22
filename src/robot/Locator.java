package robot;

import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.robotics.navigation.Pose;

/**
 * Locator is just a locator :)
 * @author Phuoc Nguyen, Khoa Tran
 **/
public class Locator
{ 
	// Instances
	private float _hallWidth = 241f;
	private float _beaconToSouthWall = 241f;
	private Point[] _beaconLocations = {new Point(0,0), new Point(0,_beaconToSouthWall)};
	public Pose _pose = new Pose();
	public float echoDistance;
	public float[] _beaconBearing = new float[2];
	private Scanner _scanner;
	
	// use to shift
	private float _differenceXFactor = 5.5f;
	private float _differenceYFactor = 0f;
	private float _differenceAngleFactor = 0f;
	
	
	/**
	 * Constructor for the Locator
	 * @param scan: scanner
	 */
	public Locator(Scanner scan) {
		_scanner = scan;
	}

	/**
	 * Set the pose in the locator into the input Pose
	 * @param p: pose to set
	 */
	public void setPose(Pose p) {
		_pose.setLocation(p.getX(),p.getY());
		_pose.setHeading(p.getHeading());
	}

	/**
	 * Locate where the robot is first, before doing fix or shift or anything else
	 * In this actual Locate function, we will actually shift the robot to the location
	 * that we get from calculating from fixPoint
	 */
	public void locateMeAndShiftMe() {
		float x = _pose.getX();
		float y = _pose.getY();
		float angleToZeroWall = _pose.relativeBearing(new Point(x, 0)) + _differenceAngleFactor;
		float angleToYWall = _pose.relativeBearing(new Point(x, _hallWidth)) + _differenceAngleFactor;
		int maxEchoDistance = 255; 
		float[] angleToBeacons = {0f, 0f};
		boolean isNearSouthWall = y < (_hallWidth / 2);

		if (isNearSouthWall) {
			maxEchoDistance = _scanner.getEchoDistance(angleToZeroWall);
			angleToBeacons = findBeaconBearings();
		} else {
			maxEchoDistance = (int) _hallWidth - _scanner.getEchoDistance(angleToYWall);
			angleToBeacons = findBeaconBearings();
			
			//swap the value because it scans the other way
			float temp = angleToBeacons[0];
			angleToBeacons[0] = angleToBeacons[1];
			angleToBeacons[1] = temp;
		}

		//After getting the location based on calculation, we will fixPosition
		fixPosition(angleToBeacons, (float) maxEchoDistance);
		
		// I'm not happy, so I try to shift it and set the heading
		_pose.setLocation(_pose.pointAt(_differenceXFactor, _pose.getHeading() + 180));
	}

	public void printPose() {
		System.out.println("X: " + _pose.getX());
		System.out.println("Y: " + _pose.getY());
		System.out.println("H: " + _pose.getHeading());
	}

	/**
	 * Depend on the current Position, find the way to scan 2 beacons
	 * @return bearings to 2 beacons
	 */

	public float[] findBeaconBearings() {
		float x = _pose.getX();
		float y = _pose.getY();
		float angleToZeroWall = _pose.relativeBearing(new Point(x, 0)) + _differenceAngleFactor;
		float angleToYWall = _pose.relativeBearing(new Point(x, _hallWidth)) + _differenceAngleFactor;
		boolean isFirstQuadrant = (x >= 0);
		boolean isNearSouthWall = y < (_hallWidth / 2);
		
		if (isFirstQuadrant) {
			if (isNearSouthWall) {
				_scanner.lightScan((int) angleToZeroWall, (int) angleToZeroWall - 180);
			} else {
				_scanner.lightScan((int) angleToYWall, (int) angleToYWall + 180);
			}
		} else {
			if (isNearSouthWall) {
				_scanner.lightScan((int) angleToZeroWall, (int) angleToZeroWall + 180);
			} else {
				_scanner.lightScan((int) angleToYWall, (int) angleToYWall - 180);
			}
		}

		int[] intBearings = _scanner.getBearings();
		float[] floatBearings = convertBearings(intBearings, isFirstQuadrant, isNearSouthWall);
		
		return floatBearings;
	}
	
	/**
	 * Because the original code was used mostly for int, we need to convert it to float
	 * @param intBearings: original Bearing in int
	 * @param isFirstQuadrant: is it?
	 * @param isNearSouthWall: is it?
	 * @return floatBearings based on where it is
	 */
	public float[] convertBearings(int [] intBearings, boolean isFirstQuadrant, boolean isNearSouthWall){
		float[] floatBearings = {0f, 0f};
		for (int i = 0; i < 2; i++) {
			if ((isFirstQuadrant) == (isNearSouthWall)) {
				floatBearings[i] = (float) intBearings[1-i];
			} else {
				floatBearings[i] = (float) intBearings[i];
			}
		}
		return floatBearings;
	}
	
	/**
	 * Get the scanner
	 * 
	 * @return the Scanner
	 */
	public Scanner getScanner() {
		return _scanner;
	}


	/**
	 * Do all the calculation for the new Pose, and set this to the current Pose
	 */

	public Pose fixPosition(float[] bearings, float echoDistance){
		float x = 0;
		float y = echoDistance + _differenceYFactor;
		float y0 = y;
		float y1 = _beaconToSouthWall - y;
		double c = Math.toRadians(normalize(bearings[0] - bearings[1]));
		double theta = Math.abs( Math.abs(c) - 180);
		double delta = Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4*y0*y1);

		float x1 = (float) (_beaconToSouthWall * Math.tan((Math.PI / 2) - (c/2)) / 2);
		float x2 = (float) (0.5 * ( ((y0 + y1) / Math.tan(c)) + Math.sqrt( delta ) ));
		float x3 = (float) (0.5 * ( ((y0 + y1) / Math.tan(c)) - Math.sqrt( delta ) ));
		
		if (theta <= 2) {
			x = x1;
		} else if (c > 0) {
			x = x2;
		} else if (c <= 0) {
			x = x3;
		}

		_pose.setLocation(x,y);
		float heading = normalize(_pose.angleTo(_beaconLocations[0]) - bearings[0]);
		_pose.setHeading(heading);

		return _pose;
	}

	/**
	 *returns angle between -180 and 180 degrees
	 */	
	private float normalize(float angle){
		while(angle<-180)angle+=360;
		while(angle>180)angle-=360;	
		return angle;
	}
}

