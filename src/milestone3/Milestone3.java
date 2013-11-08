package milestone3;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
import lejos.util.Datalogger;
import lejos.util.Delay;
import robot.Locator;
import robot.Scanner;

public class Milestone3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LightSensor ls = new LightSensor(SensorPort.S2);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
		Scanner scanner = new Scanner(Motor.B, ls, us);
		Locator locator = new Locator(scanner);

		double leftWheelDiameter = 5.42;
		double rightWheelDiameter = 5.44;
		double trackWidth = 13.72;
		DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);
		pilot.setAcceleration(1500);
		pilot.setTravelSpeed(30);
		pilot.setRotateSpeed(360);

		Datalogger dl = new Datalogger();
		Pose p = new Pose();
		p.setLocation(240, 185);
		
		Button.waitForAnyPress();

		// Try out each of the 4 headings: 0, 90, 180, -90
		for (int i = 0; i < 4; i++) {
			p.setHeading((float) 90 * i);
			for (int j = 0; j < 2; j++) { // repeat 8 times for each heading
				locator.setPose(p);
				locator.locate();
				dl.writeLog(locator._pose.getX(), locator._pose.getY(), locator._pose.getHeading());
				Delay.msDelay(100);
			}
			pilot.rotate(90);
		}
		
		LCD.clearDisplay();
		System.out.println("Press any button to start transmitting...");
		Button.waitForAnyPress();
		dl.transmit();
	}
}
