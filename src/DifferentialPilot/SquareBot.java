package DifferentialPilot;
import lejos.nxt.*;
import lejos.util.Delay;

/**
 * This class can trace a square of any size and in any direction. Uses
 * individual Motors
 * 
 * @author Khoa Tran, Peter Nguyen
 */
public class SquareBot {
	/**
	 * Constructs the robot and drives it to meet the project specifications
	 * @param args the command line arguments           
	 */
	public static void main(String[] args) {
		LCD.drawString("GO", 0, 0);
		Button.waitForAnyPress();
		SquareBot robot = new SquareBot();
		Motor.A.setSpeed(350);
		Motor.A.setAcceleration(1000);
		Motor.C.setSpeed(350);
		Motor.C.setAcceleration(1000);
		int direction = 1;
		int length = 90;
		int flag = 0;
		robot.square(length, direction);

		for (int i = 1; i < 4; i++) {
			if (i < 2) {
				robot.square(length, direction);	// go straight forward
			} else {
				// Perform ONLY ONE backward turn
				if ( flag == 0 ){
					Motor.C.rotate(-sqrAngle);
					flag++;
				}				
				robot.square(length, -direction);	// go reversely
			}
		}
	}

	/**
	 * Top level task: trace a square of specified size, and direction
	 * @param length ---> will be given
	 * @param direction ---> 1: forward, -1: reverse
	 */
	public void square(float length, int direction) {
		if ( direction == 1 ){
			for ( int i = 0; i < 4; i++ ){
			goMotors(length);
			Motor.A.rotate(sqrAngle);
			}
		}
		else {
			for ( int i = 0; i < 4; i++ ){
			goMotors(length);
			Motor.C.rotate(sqrAngle);
			}
		}
	}
	
	public void goMotors(float length) {
		Motor.A.forward();
		Motor.C.forward();
		Delay.msDelay((long) (length/(Math.PI*wheelDiameter))*1000);		
		Motor.A.stop(true);
		Motor.C.stop();
	}

	// instance variables -- check these values for your robot
	float wheelDiameter = 5.6f;
	float trackWidth = 14.1f;
	static int sqrAngle = 365; 	// sqrAngle hold the value of angle that make the robot turns 90 degrees
}
