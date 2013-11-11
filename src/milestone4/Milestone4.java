package milestone4;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import robot.*;

public class Milestone4 {

	public static void main(String[] args) {
		double leftWheelDiameter = 5.415;
		double rightWheelDiameter = 5.445;
		double trackWidth = 13.65;

		DifferentialPilot dp = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);

		dp.setAcceleration(300);
		dp.setTravelSpeed(20);
		dp.setRotateSpeed(100);
		
		Navigator navigator = new Navigator(dp);
		
		Scanner scanner = new Scanner(Motor.B, new LightSensor(SensorPort.S2), new UltrasonicSensor(SensorPort.S3));
		Locator locator = new Locator(scanner);
		//TouchDetector detector = new TouchDetector(SensorPort.S1, SensorPort.S4);
		
		Controller controller = new Controller(navigator, locator);
		controller.go();
	}

}
