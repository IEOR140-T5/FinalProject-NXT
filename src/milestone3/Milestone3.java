package milestone3;

import lejos.nxt.Button;
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

	public static void main(String[] args) {
		// Declare objects we built
		LightSensor ls = new LightSensor(SensorPort.S2);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
		Scanner scanner = new Scanner(Motor.B, ls, us);
		Locator locator = new Locator(scanner);

		// Some other configurations for Differential Pilot
		double leftWheelDiameter = 5.36;
		double rightWheelDiameter = 5.38;
		double trackWidth = 13.28;
		DifferentialPilot pilot = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);
		pilot.setAcceleration(1200);
		pilot.setTravelSpeed(30);
		pilot.setRotateSpeed(360);

		Datalogger dl = new Datalogger();
		Pose p = new Pose();
		p.setLocation(-30, 35);
		//p.setLocation(240, 185);
		
		Button.waitForAnyPress();

		
		for (int i = 0; i < 4; i++) {
			p.setHeading((float) 90 * i);
			for (int j = 0; j < 8; j++) { 
				locator.setPose(p);
				//locator.locate(); // bearings is only used for report
				dl.writeLog(locator._pose.getX(), locator._pose.getY(), locator._pose.getHeading());
				dl.writeLog(locator._beaconBearing[0], locator._beaconBearing[1]);
				Delay.msDelay(700);
			}
			pilot.rotate(90);
		}
		
		// stop to transmit data
		Button.waitForAnyPress();
		System.out.println("Press to continue");
		Button.waitForAnyPress();
		dl.transmit();
	}
}
