package robot;

import java.io.IOException;
import java.util.ArrayList;

import robot.Message;
import robot.MessageType;
import lejos.geom.Point;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

/**
 * Controls the robot by coordinating messages that come in to 
 * the bluetooth communicator with actions that must be performed
 * by the navigator or the locator
 * 
 * Also listens to the touch detector and tells the robot to 
 * back up when it hits something
 */
public class Controller implements CommListener {
	private Navigator navigator;
	private Communicator comm;
	private ArrayList<Message> inbox;
	private Locator locator;

	public Controller(Navigator n, Locator l) {
		System.out.println("Connecting...");
		comm = new Communicator();
		comm.setController(this);
		navigator = n;
		locator = l;
		inbox = new ArrayList<Message>();
		//navigator.addWaypoint(0, 0);
	}

	/**
	 * Puts the given message in the robot controller's inbox.
	 * 
	 * For any messages besides STOP, they will be read first-in first-out
	 * A STOP message will clear the controller's inbox and its current path
	 * and tell the navigator to stop.
	 * 
	 * @param m the message to be put in the inbox
	 */
	public void updateMessage(Message m) {
		System.out.println("Updating messages...");
		if(m.getType() == MessageType.STOP) {
			inbox.clear();
			navigator.stop();
			navigator.clearPath();
		} else {
			inbox.add(m);
		}
	}

	/**
	 * Listens to the inbox for new messages, executes and 
	 * removes messages as they come in.
	 */
	public void go() {
		Message currentMessage = new Message(MessageType.STOP, new float[1]);
		while(true) {
			//System.out.println(currentMessage.getType());
			while(!inbox.isEmpty()) {
				System.out.println("Running message!");
				currentMessage = inbox.remove(0);
				execute(currentMessage);
			}
		}
	}
	
	/**
	 * Gets the current pose from the navigator and passes a message
	 * to the communicator for it to send to the PC
	 */
	private void sendPose() {
		Pose pose = navigator.getPoseProvider().getPose();
		float[] array = new float[3];
		array[0] = pose.getX();
		array[1] = pose.getY();
		array[2] = pose.getHeading();
		try {
			comm.send(new Message(MessageType.POS_UPDATE, array));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a CRASH message to the PC
	 */
	private void sendCrashMsg() {
		try {
			comm.send(new Message(MessageType.CRASH, null));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends pose, if desired, every 300 ms, as well as sends
	 * the echo distance in the direction the head is currently facing every
	 * 50 ms, if desired.
	 * 
	 * @param sendPose if true, the robot will send the pose every 300 ms
	 * @param sendObstacles if true, the robot will send the echo dist every 50 ms
	 * 
	 * Send Obstacles is current not implemented
	 */
	private void sendData(boolean sendPose, boolean sendObstacles) {
		while (navigator.isMoving() || navigator.getMoveController().isMoving()) {
			int obstacleDist;
			int headAngle = locator.getScanner().getHeadAngle();
			for (int i = 0; i < 6; i++) {
				Delay.msDelay(50);
				obstacleDist = locator.getScanner().getDistance();
				if ((i == 5) && (sendPose)) {
					sendPose();
				}
			}
		}
		sendPose();
	}

	/**
	 * Parses the given message and acts on it accordingly.
	 * 
	 * STOP: stop the robot
	 * MOVE: calls the navigator to move to a given x,y
	 * FIX_POSE: fixes the position based on the bearings and echo distance
	 * SET_POSE: updates the locator's and navigator's pose to a given pose
	 * 
	 * @param m - the message that represents which action to be executed
	 */
	private void execute(Message m) {
		Sound.playNote(Sound.PIANO, 450, 15);
		switch(m.getType()) {
		case STOP:
			navigator.stop();
			sendPose();
			break;
		case MOVE:
			float[] data = m.getData();
			navigator.goTo(data[0], data[1]);
			sendData(true, false);
			break;
		case FIX_POS:
			System.out.println("fix pos start");
			locator.setPose(navigator.getPoseProvider().getPose());
			System.out.println("fix pos got the pose from nav");
			locator.locate();
			System.out.println("locator located");
			navigator.getPoseProvider().setPose(locator._pose);
			sendPose();
			System.out.println("fix pos end");
			break;
		case ROTATE:
			((DifferentialPilot) navigator.getMoveController()).rotate(m.getData()[0]);
			sendPose();
			break;
		case TRAVEL:
			((DifferentialPilot) navigator.getMoveController()).travel(m.getData()[0], true);
			sendData(true, false);
			break;
		case SET_POSE:
			locator._pose.setLocation(m.getData()[0], m.getData()[1]);
			locator._pose.setHeading(m.getData()[2]);
			navigator.getPoseProvider().setPose(locator._pose);
			sendPose();
			break;
		default:
			break;
		}
		Sound.playNote(Sound.PIANO, 500, 15);
	} 

	/**
	 * Actions when we find an object at the given location
	 */
	public void objectFound(Point obstacleLocation) {		
		sendCrashMsg();
		navigator.stop();
		navigator.getMoveController().stop();
		navigator.clearPath();
		navigator.getMoveController().travel(-5);
		sendPose();
	}
}
