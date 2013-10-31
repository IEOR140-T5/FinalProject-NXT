package milestone2;

import robot.Scanner;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Datalogger;

public class ScanTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Create a scanner and a Datalogger.
		Scanner scanner = new Scanner(Motor.B, new LightSensor(SensorPort.S2),
				new UltrasonicSensor(SensorPort.S3));
		Datalogger dl = new Datalogger();

		for (int times = 0; times < 2; times++) {
			// Scan 8 times at the far point, then the near point
			for (int i = 0; i < 8; i++) {
				Button.waitForAnyPress();
				scanner.scanLights(-180, 180);
				int[] bearings = scanner.getBearings();
				dl.writeLog(bearings[0], bearings[1]);
				System.out.println("(" + bearings[0] + ", " + 
									bearings[1] + ")");
			}

			// Transmit
			System.out.println("Transmitting " + times + "...");
			Button.waitForAnyPress();
			dl.transmit();
		}
	}
}
