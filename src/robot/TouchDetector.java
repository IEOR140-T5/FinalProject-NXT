package robot;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;

/**
 * Detects touches made on the touch sensors located on the front of the robot.
 * 
 * @author nate.kb
 *
 */
public class TouchDetector {

	private TouchSensor lb;
	private TouchSensor rb;
	private CommListener listener;
}
