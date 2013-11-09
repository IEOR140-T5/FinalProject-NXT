package robot;

import lejos.geom.Point;

/**
 * An interface for something that listens to the obstacle detector.
 */
public interface CommListener {

	/**
	 * Detects an obstacle through a touch sensor or an ultrasonic sensor.
	 * 
	 * @param objectLocation - the location of the object
	 */
	public void objectFound(Point objectLocation);
}
