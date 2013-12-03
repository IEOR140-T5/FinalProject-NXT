package robot;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;

/**
 * This class will take care of the Touchsensor and reponse back to the controller
 * whether the Touchsensor is touched. It is better that we know which sensor is touched
 * (left/right) so we can literally map where we hit the object.
 * @author Phuoc Nguyen, Khoa Tran
 *
 */
public class Detector {

	// Instances
	private TouchSensor leftTouchsensor;
	private TouchSensor rightTouchsensor;
	private Controller detectorListener;
	
	/**
	 * Constructor for the TouchsensorDetector
	 * @param leftTouchSensorPort - port for left
	 * @param rightTouchSensorPort - port for right 
	 */
	public Detector(SensorPort leftTouchSensorPort, SensorPort rightTouchSensorPort) {
		leftTouchsensor = new TouchSensor(leftTouchSensorPort);
		rightTouchsensor = new TouchSensor(rightTouchSensorPort);
		leftTouchSensorPort.addSensorPortListener(new TouchsensorDetectorListener(true, false));
		rightTouchSensorPort.addSensorPortListener(new TouchsensorDetectorListener(false, true));
	}
	
	/**
	 * Connect the detector to the current Controller in order to get
	 * the response back from the Detector
	 * @param listener - the Controller class
	 */
	public void setControllerListener(Controller listener) {
		detectorListener = listener;
	}
	
	/**
	 * This is an inner class playing a role as a Listener and implement the 
	 * given SensorPortListener class. It will response back to the controller
	 * whether the Touchsensor is touched or not, in real-time.
	 */
	private class TouchsensorDetectorListener implements SensorPortListener {
		
		// Instaces to determine which sensor is touched
		private boolean isLeft;
		private boolean isRight;


		/**
		 * Constructor, determine is it the right listener or left listener
		 * @param left - true/false: is it?
		 * @param right - true/false: is it? 
		 */
		public TouchsensorDetectorListener(boolean left, boolean right) {
			isLeft = left;
			isRight = right;
		}
		
		/**
		 * Inherited from the original SensorPortListener class
		 * Will reponse back when the sensor is touched or released
		 */
		@Override
		public void stateChanged(SensorPort port, int aOldValue, int aNewValue) {
			if (aNewValue > 1000){
				System.out.println("Released!");
				Sound.playNote(Sound.PIANO, 600, 50);
			} else {
				System.out.println("Touched! ");
				Sound.playNote(Sound.PIANO, 500, 50);
				if (isLeft){
					detectorListener.touchSensorTouched(true, false);
				} else {
					detectorListener.touchSensorTouched(false, true);
				}
			}
		}

	}
}
