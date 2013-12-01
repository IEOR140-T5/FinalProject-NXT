package robot;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;


public class TouchDetector {

	private TouchSensor lb;
	private TouchSensor rb;
	private Controller listener;
	

	public TouchDetector(SensorPort leftTouchSensorPort, SensorPort rightTouchSensorPort) {
		lb = new TouchSensor(leftTouchSensorPort);
		rb = new TouchSensor(rightTouchSensorPort);
		leftTouchSensorPort.addSensorPortListener(new TouchDetectorListener(true));
		rightTouchSensorPort.addSensorPortListener(new TouchDetectorListener(false));
	}
	

	public void setObstacleListener(Controller l) {
		listener = l;
	}
	
	/**
	 * Inner Class
	 * @author 
	 *
	 */
	private class TouchDetectorListener implements SensorPortListener {

		private boolean isLeft;
		private boolean isRight = false;


		public TouchDetectorListener(boolean left) {
			isLeft = left;
			isRight = false;
		}


		public void stateChanged(SensorPort port, int aOldValue, int aNewValue) {
			if ((aNewValue < 190) && (aOldValue > 190)) {
				System.out.println("Touched! " + aOldValue + " " + aNewValue);
				Sound.playNote(Sound.PIANO, 500, 50);
				if (isLeft){
					listener.touchSensorTouched(true, false);
				} else {
					listener.touchSensorTouched(false, true);
				}
			} else if ((aNewValue > 1000) && (aOldValue > 0)) {
				System.out.println("Released!" + aOldValue + " " + aNewValue);
				Sound.playNote(Sound.PIANO, 600, 50);
			}
		}

	}
}
