package robot;
import lejos.geom.Point;
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
	static final float differenceFactor = 5.5f; // it's how off it's from the center of the robot when rotating

	// 2 Points of the 2 beacons
	Point[] beacon = { new Point(0, 0), new Point(0, beaconY) };
	//public Pose _pose = new Pose();
	//public float echoDistance;
	public float[] _beaconBearing = new float[2];
	private Scanner scanner;
	public VariancePose _pose = new VariancePose();
	
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
		float angleError = 0f;  //hard code, whenever needed
		float angleToNorthWall = normalize(_pose.relativeBearing(new Point(x, 0))) + angleError;
		float angleToSouthWall = normalize(_pose.relativeBearing(new Point(x, hallWidth))) + angleError;
		
		// it's when x >=0 in the first Quadrant
		boolean isFirstQuadrant = (x >=0);
		// its when Y location is near the zero beacon
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
	public float[] locate() {
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

		// for debugging
		_beaconBearing = beaconsBearings;
		System.out.println(distanceToWall +"," + beaconsBearings[0] + "," + beaconsBearings[1]);

		// calculate the Pose
		//_pose = fixPosition(beaconsBearings, (float) distanceToWall);
		calculateOptimal(fixPosition(beaconsBearings, (float) distanceToWall));

		// shift the location of the pose and set new heading
		_pose.setLocation(_pose.pointAt(differenceFactor, _pose.getHeading() + 180));
		
		return beaconsBearings;
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
	/*
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
		
		System.out.println(x1 + "-" + x2);
		x = (Math.abs(differenceInDeg) < 90) ? x1 : x2;
		x = (isDifferenceInDegNegative) ? x*-1 : x*1;
		

		float newHeading = normalize(_pose.angleTo(beacon[0]) - bearings[0]);
		_pose.setHeading(newHeading);

		// hardcode heading
		/*
		if (_pose.getHeading() == 0){
			_pose.setHeading(normalize(_pose.angleTo(beacon[0]) - bearings[0]) - 10);
		}
		if (_pose.getHeading() == 90){
			_pose.setHeading(normalize(_pose.angleTo(beacon[0]) - bearings[0]) - 10);
		}
		if (_pose.getHeading() == 180){
			_pose.setHeading(normalize(_pose.angleTo(beacon[0]) - bearings[0]) + 10);
		}
		if (_pose.getHeading() == 270){
			_pose.setHeading(normalize(_pose.angleTo(beacon[0]) - bearings[0]) + 10);
		} 0----
		
		_pose.setLocation(
				(float) (x - Math.cos(Math.toRadians(newHeading))) - 6, echoDistance);

		return _pose;
	}*/
	

    public Pose fixPosition(float[] bearings, float echoDistance) {
            float y = echoDistance;
            float y0 = y;
            float x = 0;
            float y1 = beaconY - y;

            double c = Math.toRadians(normalize(bearings[0] - bearings[1]));
            double theta = Math.abs(c);
            boolean delta = Math.abs(theta - 180) <= 2;
            //boolean delta = Math.abs(theta - 180) <= 0;
            

            if (delta) {
                    x = (float) (beaconY * Math.tan((Math.PI / 2) - (c / 2)) / 2);
            } else if (c > 0) {
                    x = (float) (0.5 * (((y0 + y1) / Math.tan(c)) + Math.sqrt(Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4 * y0 * y1))));
            } else if (c <= 0) {
                    x = (float) (0.5 * (((y0 + y1) / Math.tan(c)) - Math.sqrt(Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4 * y0 * y1))));
            }

            _pose.setLocation(x, y);
            float heading = normalize(_pose.angleTo(beacon[0]) - bearings[0]);
            _pose.setHeading(heading);

            return _pose;
    }
	

/**
** Calculate the optimal by parsing in the Pose
** @param p: the pose
** @return new pose
**/
    public Pose calculateOptimal(Pose p) {
            // variances from milestone 3 scan
            float myVarX = 0.3f;
            float myVarY = 0.7f;
            float myVarH = 0.6f;

            float varX = _pose.getVarX();
            float varY = _pose.getVarY();
            float varH = _pose.getVarH();

            float xL = p.getX();
            float yL = p.getY();
            float hL = p.getHeading();

            float optH = (varH * hL + myVarH * _pose.getHeading())
                            / (varH + myVarH);
            float optX = (varX * xL + myVarX * _pose.getX()) / (varX + myVarX);
            float optY = (varY * yL + myVarY * _pose.getY()) / (varY + myVarY);

            return new Pose(optX, optY, optH);
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
	
	/**
	 * Print the current pose
	 */
	public void printPose() {
		System.out.println("X: " + _pose.getX());
		System.out.println("Y: " + _pose.getY());
		System.out.println("Heading: " + _pose.getHeading());
	}
	
	/**
	 * @return the scanner
	 */
	public Scanner getScanner() {
		return scanner;
	}
}