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

		DifferentialPilot dp = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);

		//dp.setAcceleration(300);
		dp.setAcceleration(40);
		dp.setTravelSpeed(20);
		dp.setRotateSpeed(100);
		
		/**
		 * Declares objects that the robot needs to operate, and go go go
		 */
        //VariancePoseProvider variance = new VariancePoseProvider(dp, 0.25f, 4f);
        //Navigator navigator = new Navigator(dp, variance);
		Navigator navigator = new Navigator(dp);
		
		Scanner scanner = new Scanner(Motor.B, new LightSensor(SensorPort.S2), 
				new UltrasonicSensor(SensorPort.S3));
		Locator locator = new Locator(scanner);
		Detector detector = new Detector(SensorPort.S1, SensorPort.S4);
		
		Controller controller = new Controller(navigator, locator, detector);
		controller.go();
	}
}
