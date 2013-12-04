package milestone6;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import robot.*;

public class Milestone6 {

	public static void main(String[] args) {
		
		/**
		 * Sets the robot parameters
		 */
		float leftWheelDiameter = 5.23f;
		float rightWheelDiameter = 5.22f;
		float trackWidth = 13.15f;

		DifferentialPilot diffPilot = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);

		diffPilot.setAcceleration(40);
		diffPilot.setTravelSpeed(20);
		diffPilot.setRotateSpeed(100);
		
		/**
		 * Declares objects that the robot needs to operate, and go go go
		 */
        VariancePoseProvider variance = new VariancePoseProvider(diffPilot, 0.25f, 4f);
        Navigator navigator = new Navigator(diffPilot, variance);
		//Navigator navigator = new Navigator(dp);
		
		Scanner scanner = new Scanner(Motor.B, new LightSensor(SensorPort.S2), 
				new UltrasonicSensor(SensorPort.S3));
		Locator locator = new Locator(scanner);
		Detector detector = new Detector(SensorPort.S1, SensorPort.S4);
		
		Controller controller = new Controller(navigator, locator, detector);
		controller.go();
	}
}
