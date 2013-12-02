package robot;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;


public class TouchDetector {

	private TouchSensor leftTouchsensor;
	private TouchSensor rightTouchsensor;
	private Controller detectorListener;
	

	public TouchDetector(SensorPort leftTouchSensorPort, SensorPort rightTouchSensorPort) {
		leftTouchsensor = new TouchSensor(leftTouchSensorPort);
		rightTouchsensor = new TouchSensor(rightTouchSensorPort);
		leftTouchSensorPort.addSensorPortListener(new TouchDetectorListener(true, false));
		rightTouchSensorPort.addSensorPortListener(new TouchDetectorListener(false, true));
	}
	

	public void setTouchDetectorListener(Controller listener) {
		detectorListener = listener;
	}
	
	/**
	 * Inner Class
	 * @author 
	 *
	 */
	private class TouchDetectorListener implements SensorPortListener {

		private boolean isLeft;
		private boolean isRight;


		public TouchDetectorListener(boolean left, boolean right) {
			isLeft = left;
			isRight = right;
		}

		public void stateChanged(SensorPort port, int aOldValue, int aNewValue) {
			if ((aNewValue < 190) && (aOldValue > 190)) {
				System.out.println("Touched! " + aOldValue + " " + aNewValue);
				Sound.playNote(Sound.PIANO, 500, 50);
				if (isLeft){
					detectorListener.touchSensorTouched(true, false);
				} else {
					detectorListener.touchSensorTouched(false, true);
				}
			} else if ((aNewValue > 1000) && (aOldValue > 0)) {
				System.out.println("Released!" + aOldValue + " " + aNewValue);
				Sound.playNote(Sound.PIANO, 600, 50);
			}
		}

	}
}
