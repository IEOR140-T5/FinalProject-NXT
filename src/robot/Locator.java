package robot;
import lejos.geom.Point;
import lejos.nxt.LCD;
import lejos.robotics.navigation.Pose;

/**
 * Starting point navigation lab - for testing the fix method R. Glassey 10/08
 **/
public class Locator {
	
	/**
	 * Fields and static variables
	 */
	static final float hallWidth = 239f; // cm - check with scanner
	static final float beaconY = 229f;   // hallWidth -10
	static final float distanceToAxleLength = 5.5f;

	Point[] beacon = { new Point(0, 0), new Point(0, beaconY) };
	public Pose _pose = new Pose();
	public float echoDistance;
	public float[] _beaconBearing = new float[2];
	private Scanner scanner;
	
	/**
	 * Constructor that takes in a Scanner as parameter
	 * @param s - the scanner to be initialized and set
	 */
	public Locator(Scanner s) {
		scanner = s;
	}

	/**
	 * Sets the current pose to some Pose p
	 * @param p - the pose to be set to
	 */
	public void setPose(Pose p) {
		_pose.setLocation(p.getX(),p.getY());
		_pose.setHeading(p.getHeading());
	}
	
	/**
	 * Sets beaconBearing array based on current position. 
	 * In your robot, you will use the scanner to get this data
	 */
	float[] scanBeacons() {
		float x = _pose.getX();
		float y = _pose.getY();
		float angleToZeroWall = normalize(_pose.relativeBearing(new Point(x, 0)));
		float angleToYWall = normalize(_pose.relativeBearing(new Point(x, hallWidth)));

		if (x >= 0) {
			if (y < hallWidth/2) {
				scanner.scanLights((int) angleToZeroWall, (int) angleToZeroWall - 180);
			} else {
				scanner.scanLights((int) angleToYWall, (int) angleToYWall + 180);
			}
		} else {
			if (y < hallWidth/2) {
				scanner.scanLights((int) angleToZeroWall, (int) angleToZeroWall + 180);
			} else {
				scanner.scanLights((int) angleToYWall, (int) angleToYWall - 180);
			}
		}

		int[] intBearings = new int[2];
		intBearings = scanner.getBearings();
		float[] bearings = {0f, 0f};
		
		// assign the int bearings to the float bearings
		for (int i = 0; i < 2; i++) {
			if ((x >= 0) == (y < hallWidth / 2)) { // if both are true or both are false
				bearings[i] = (float) intBearings[1-i];
			} else {
				bearings[i] = (float) intBearings[i];
			}
		}
		return bearings;
	}
	
	/**
	 * Locates the robot's position precisely using Scanner
	 * Chooses the correct wall to look at, and then does a 180 degree scan
	 * in the appropriate direction to find the light sources
	 */
	public void locate() {
		float x = _pose.getX();
		float y = _pose.getY();
		float angleToZeroWall = normalize(_pose.relativeBearing(new Point(x, 0)));
		float angleToYWall = normalize(_pose.relativeBearing(new Point(x, hallWidth)));

		int distanceToWall;
		float[] bearings = {0f, 0f};

		// Compare pose.getY() to hallWidth, rotate to and scan the closer wall
		if (y < (hallWidth / 2)) {
			distanceToWall = scanner.getDistanceToWall(angleToZeroWall);
			bearings = scanBeacons();
		} else {
			distanceToWall = (int) hallWidth - scanner.getDistanceToWall(angleToYWall);
			float[] tempBearings = scanBeacons();
			for (int i = 0; i < 2; i++) {
				bearings[i] = tempBearings[1 - i];
			}
		}

		LCD.drawInt(3, 4, 1);
		//System.out.println("Dist to Wall: " + distanceToWall);
		//System.out.println("Bearings: (" + bearings[0] + ", " + bearings[1] + ")");

		// Fixes the position based on bearings and the echo distance to wall
		fixPosition(bearings, (float) distanceToWall);

		// Corrects the location based on the fact that the scanning head is not directly above the wheel base
		_pose.setLocation(_pose.pointAt(distanceToAxleLength, _pose.getHeading() + 180));
	}

	/**
	 * calculates position from beacon coordinates and beacon bearing and echo distance
	 * @return a new Pose
	 */
	Pose fixPosition(float[] bearings, float echoDistance) {
		float y = echoDistance;
		float y0 = y;
		float y1 = beaconY - y;

		double c = Math.toRadians(normalize(bearings[0] - bearings[1]));
		float x = 0;

		if (Math.abs(Math.abs(c) - 180) <= 2) {
			x = (float) (beaconY * Math.tan((Math.PI / 2) - (c/2)) / 2);
		} else if (c > 0) {
			x = (float) (0.5 * ( ((y0 + y1) / Math.tan(c)) +
					Math.sqrt(Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4*y0*y1))));
		} else if (c <= 0) {
			x = (float) (0.5 * ( ((y0 + y1) / Math.tan(c)) -
					Math.sqrt(Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4*y0*y1))));
		}

		_pose.setLocation(x, y);
		float heading = normalize(_pose.angleTo(beacon[0]) - bearings[0]);
		_pose.setHeading(heading);
		System.out.println(_pose.getX());
		System.out.println(_pose.getY());
		System.out.println(_pose.getHeading());
		return _pose;
	}

	/**
	 * returns angle between -180 and 180 degrees
	 */
	private float normalize(float angle) {
		while (angle < -180) {
			angle += 360;
		}
		while (angle > 180) {
			angle -= 360;
		}
		return angle;
	}
	
	/**
	 * Prints the pose for debugging
	 */
	public void printPose() {
		System.out.println("X: " + _pose.getX());
		System.out.println("Y: " + _pose.getY());
		System.out.println("H: " + _pose.getHeading());
	}
}
