package robot;
import lejos.geom.Point;
import lejos.nxt.LCD;
import lejos.robotics.navigation.Pose;

/**
 * Locator class to locate and calculate the Pose
 **/
public class Locator {
	
	/**
	 * Fields and static variables
	 */
	static final float hallWidth = 241f; // cm - check with scanner
	static final float beaconY = 241f;
	static final float _differenceXFactor = 6.0f; // it's how off it's from the center of the robot when rotating

	// 2 Points of the 2 beacons
	Point[] beacons = { new Point(0, 0), new Point(0, beaconY) };
	public Pose _pose = new Pose();
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
	 * get the bearings of the 2 beacons
	 * @return the arrays that contains 2 bearings
	 */
	public float[] scanBeacons() {
		float x = _pose.getX();
		float y = _pose.getY();
		float angleError = 15f;
		float angleToNorthWall = normalize(_pose.relativeBearing(new Point(x, 0))) + angleError;
		float angleToSouthWall = normalize(_pose.relativeBearing(new Point(x, hallWidth))) + angleError;
		
		// it's when x >=0 in the first Quadrant
		boolean isFirstQuadrant = (x >=0);
		// it's when Y location is near the zero beacon
		boolean isYNearZeroBeacon = y < hallWidth/2;
		
		if (isFirstQuadrant) {
			if (isYNearZeroBeacon) {
				scanner.scanLights((int) angleToNorthWall, (int) angleToNorthWall - 180);
				System.out.println("1st Quad");
			} else {
				scanner.scanLights((int) angleToSouthWall, (int) angleToSouthWall + 180);
				System.out.println("2nd Quad");
			}
		} else {
			if (isYNearZeroBeacon) {
				scanner.scanLights((int) angleToNorthWall, (int) angleToNorthWall + 180);
				System.out.println("3rd Quad");
			} else {
				scanner.scanLights((int) angleToSouthWall, (int) angleToSouthWall - 180);
				System.out.println("4th Quad");
			}
		}
		 
		float[] bearings = scanner.getBearings();
		return bearings;
	}
	
	/**
	 * Locate the current coordinate of the robot
	 * It will find an appropriate heading to scan 180 degree which contains 2 lights
	 * @return the array contains 2 bearing // for milestone 3 report
	 */
	public void locate() {
		float x = _pose.getX();
		float y = _pose.getY();
		
		// North: 0 light, South: 241 light
		Point northPoint = new Point(x, 0);
		Point southPoint = new Point(x, hallWidth);
		float angleToNorthWall = normalize(_pose.relativeBearing(northPoint));
		float angleToSouthWall = normalize(_pose.relativeBearing(southPoint));

		// used for calculation
		int distanceToWall = 0;
		float[] beaconsBearings = new float[2];
		
		// find the nearest wall and base on that to calculate everything
		boolean isYNearZeroBeacon = y < (hallWidth /2);
		
		if (isYNearZeroBeacon) {
			distanceToWall = scanner.getDistanceToWall(angleToNorthWall);
			beaconsBearings = scanBeacons();
			
		} else {
			distanceToWall = (int) hallWidth - scanner.getDistanceToWall(angleToSouthWall);
			beaconsBearings = scanBeacons();
			// reverse the array because the 2 positions are switched
			beaconsBearings = reverseBeaconsBearings(beaconsBearings);
		}

		_beaconBearing = beaconsBearings;
		System.out.println(distanceToWall +"," + beaconsBearings[0] + "," + beaconsBearings[1]);

		// calculate the Pose
		_pose = fixPosition(beaconsBearings, (float) distanceToWall);	
	}
	
	/**
	 * reverse beaconBearings
	 * @param beaconsBearings array
	 * @return reversed beaconsBearings array
	 */
	public float[] reverseBeaconsBearings(float[] beaconsBearings){
		float temp = beaconsBearings[0];
		beaconsBearings[0] = beaconsBearings[1];
		beaconsBearings[1] = temp;
		return beaconsBearings;
	}

	/**
	 * Calculates position from beacon coordinates and beacon bearing and echo distance
	 * @param bearings - the bearings of the lights
	 * @param echoDistance - how far is it away from the closest wall
	 * @return a new Pose
	 */
	public Pose fixPosition(float[] bearings, float echoDistance) {
		float x = 0;
		float y1 = echoDistance;
		float y2 = beaconY - y1;
		float differenceInDeg = normalize(bearings[0] - bearings[1]);
		boolean isDifferenceInDegNegative = normalize(bearings[0] - bearings[1]) < 0;
		float differenceInRad = (float)Math.toRadians(Math.abs(differenceInDeg));

		float a = (float) Math.tan(differenceInRad);
		float b = -beaconY;
		float c = (float) -Math.tan(differenceInRad * y1 * y2);
		float delta = (float) (Math.pow(b, 2) - 4 * a * c);
		float x1 = (float) (-b + Math.sqrt(delta)) / (2 * a);
		float x2 = (float) (-b - Math.sqrt(delta)) / (2 * a);
		float cos = (float)Math.cos(Math.toRadians(differenceInDeg));
		System.out.println(x1 + "-" + x2);
		x = (Math.abs(differenceInDeg) < 90) ? (x1-cos) : (x2-cos);
		x = (isDifferenceInDegNegative) ? x*-1 : x*1;
		

		float newHeading = normalize(_pose.angleTo(beacons[0]) - bearings[0]);
		_pose.setHeading(newHeading);
		_pose.setLocation(x - _differenceXFactor, echoDistance);
		return _pose;
	}
	
	/**
	 * Returns angle between -180 and 180 degrees
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

}
