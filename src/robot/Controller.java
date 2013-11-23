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
 * Main controller of the NXT brick, it will take in the message sent by the PC
 * via bluetooth, put it in queue, pop it and execute it
 * 
 * Listener template from professor Glassy
 */

public class Controller implements CommListener {
	private Navigator navigator;
	private Communicator communicator;
	private ArrayList<Message> queue;
	private Locator locator;
	private int _obstacleDistance = 45;

	public Controller(Navigator nav, Locator loc) {
		System.out.println("Connecting...");
		communicator = new Communicator();
		communicator.setController(this);
		navigator = nav; 
		locator = loc;
		queue = new ArrayList<Message>();
		//navigator.addWaypoint(0, 0);
	}

	/**
	 * Save the message inside the inbox to execute
	 * If the message is STOP then clear out everything
	 * 
	 * @param message: message
	 */
	public void updateMessage(Message message) {
		if(message.getType() == MessageType.STOP) {
			queue.clear();
			navigator.stop();
			navigator.clearPath();
		} else {
			queue.add(message);
		}
	}

	/**
	 * Check the queue and execute everything
	 */
	public void go() {
		Message currentMessage = new Message(MessageType.STOP, new float[1]);
		while(true) {
			//System.out.println(currentMessage.getType());
			while(!queue.isEmpty()) {
				currentMessage = queue.remove(0);
				execute(currentMessage);
			}
		}
	}
	
	/**
	 * Send the pose back to the PC to update
	 */
	private void sendPose() {
		Pose pose = navigator.getPoseProvider().getPose();
		float[] array = new float[3];
		array[0] = pose.getX();
		array[1] = pose.getY();
		array[2] = pose.getHeading();
		try {
			communicator.send(new Message(MessageType.POS_UPDATE, array));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the wall location back to PC
	 */
    private void sendWall(int obstacleDistance, int angle) {
        float[] sendBackPoint = new float[2];
        Pose pose = navigator.getPoseProvider().getPose();
        Point whereWeAre = pose.pointAt(obstacleDistance, angle + pose.getHeading());

        sendBackPoint[0] = whereWeAre.x;
        sendBackPoint[1] = whereWeAre.y;

        try {
                communicator.send(new Message(MessageType.WALL, sendBackPoint));
        } catch (IOException e) {
                System.out.println("Exception at WALL - SENDOBSTACLE");
        }
    }	
	
	
	/**
	 * Send Pose back to PC, depending on what type it is.
	 * This method is used for both sending pose back or sending pose and wall location back
	 * 
	 * @param sendPose: send Pose true/false
	 * @param sendWall: send wall location if set true
	 * 
	 */
    private void sendData(boolean sendPose, boolean sendWall) {
        while (navigator.isMoving() || navigator.getMoveController().isMoving()) {
            int obstaceDistance;
            // headAngle = getTachoCount()
            int currentHeadAngle = locator.getScanner().getHeadAngle();
            
            sendPose();
            // getEchoDistance = ultrasonic.getDistance()
            obstaceDistance = locator.getScanner().getEchoDistance();
            if (sendWall && (obstaceDistance < _obstacleDistance)) {
                sendWall(obstaceDistance, currentHeadAngle);
            }
        
        }
        sendPose();    
    }	
    
    /**
     * Ping the given current angle
     */
    private void sendEcho(){
    	int currentAngle = locator.getScanner().getHeadAngle();
    	int obstacleDistance = locator.getScanner().getEchoDistance(currentAngle);
    	sendWall(obstacleDistance, currentAngle);
    }
    
    /**
     * Ping the surrounding area
     */
    private void sendPingAll(){
    	int startAngle = 90;
    	int endAngle = -90;
    	locator.getScanner().rotateHeadTo(startAngle);
    	locator.getScanner().rotateHeadTo(endAngle);
    	while (locator.getScanner().getMotor().isMoving()){
    		int obstaceDistance;
    		int currentHeadAngle = locator.getScanner().getHeadAngle();
            obstaceDistance = locator.getScanner().getEchoDistance();
            if (obstaceDistance < 100) {
                sendWall(obstaceDistance, currentHeadAngle);
            }
    	}
    	
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
			System.out.println("FIX POSE");
			locator.setPose(navigator.getPoseProvider().getPose());
			locator.locateMeAndShiftMe();
			navigator.getPoseProvider().setPose(locator._pose);
			sendPose();
			break;
		case ROTATE:
			System.out.println("ROTATE");
			((DifferentialPilot) navigator.getMoveController()).rotate(m.getData()[0]);
			sendPose();
			break;
		case TRAVEL:
			System.out.println("TRAVEL");
			((DifferentialPilot) navigator.getMoveController()).travel(m.getData()[0], true);
			sendData(true, false);
			break;
		case SET_POSE:
			System.out.println("SET POSE");
			locator._pose.setLocation(m.getData()[0], m.getData()[1]);
			locator._pose.setHeading(m.getData()[2]);
			navigator.getPoseProvider().setPose(locator._pose);
			sendPose();
			break;
		case SEND_MAP:
            //Pose starterPose = navigator.getPoseProvider().getPose();
			System.out.println("MAPPING");
            locator.getScanner().rotateHeadTo(m.getData()[2]);
			
			//locator.getScanner().rotate(m.getData()[2], false);
            navigator.goTo(m.getData()[0], m.getData()[1]);
            sendData(true, true);
			break;
		case ECHO:
			System.out.println("ECHOING");
			sendEcho();
			break;
		case EXPLORE:
			System.out.println("PINGING");
			sendPingAll();
			break;
		default:
			System.out.println("MESSAGE NOT IN THE LIST");
			break;
		}
	} 
}
