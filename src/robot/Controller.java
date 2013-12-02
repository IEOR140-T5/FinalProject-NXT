package robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import robot.TouchsensorDetector;
import robot.Message;
import robot.MessageType;
import lejos.geom.Point;
import lejos.nxt.Sound;
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
	private int _obstacleDistance = 245;
	private TouchsensorDetector detector;

	/**
	 * Constructor that takes in Navigator and Locator
	 * @param nav
	 * @param loc
	 */
	public Controller(Navigator nav, Locator loc) {
		System.out.println("Connecting...");
		communicator = new Communicator();
		communicator.setController(this);
		navigator = nav; 
		locator = loc;
		queue = new ArrayList<Message>();
	}
	
	/**
	 * Constructor that takes in Navigator and Locator and TouchDetector
	 * @param nav
	 * @param loc
	 * @param td
	 */
	public Controller(Navigator nav, Locator loc, TouchsensorDetector td) {
		System.out.println("Connecting...");
		communicator = new Communicator();
		communicator.setController(this);
		navigator = nav; 
		locator = loc;
		detector = td;
		detector.setControllerListener(this);
		queue = new ArrayList<Message>();
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
	
    public void touchSensorTouched(boolean isLeftTouched, boolean isRightTouched) {
    	int distance = 18;
    	int angleToWall = 25;
        if (isLeftTouched){
        	sendWall(distance, -angleToWall);
        } else {
        	sendWall(distance, angleToWall);
        	
        }
        sendTouchedState();
        navigator.stop();
        //navigator.getMoveController().stop();
        navigator.clearPath();
        //navigator.getMoveController().travel(-5);
        sendPose();
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
	 * Send the message back to PC and let it hand this case
	 * either prints out the message to let the user knows
	 * or do something else
	 */
    private void sendTouchedState() {
        try {
                communicator.send(new Message(MessageType.WALLDETECTED, null));
        } catch (IOException e) {
                System.out.println("Exception at sendTouchedState");
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
                System.out.println("Exception at SENDWALL");
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
     * Ping the given angle
     */
    private void sendEcho(float angle){
    	locator.getScanner().rotateHeadTo(angle);
    	int obstacleDistance = locator.getScanner().getEchoDistance(angle);
    	sendWall(obstacleDistance, (int)angle);
    }
    
    /**
     * Ping the surrounding area
     */
    private void sendPingAll(){
    	int startAngle = 90;
    	int endAngle = -90;
    	locator.getScanner().rotateHeadTo(startAngle);
    	Delay.msDelay(200);
    	locator.getScanner().rotateTo(endAngle, true);
    	while (locator.getScanner().getMotor().isMoving()){
    		int obstaceDistance;
    		int currentHeadAngle = locator.getScanner().getHeadAngle();
            obstaceDistance = locator.getScanner().getEchoDistance();
            if (obstaceDistance < _obstacleDistance) {
                sendWall(obstaceDistance, currentHeadAngle);
            }
    	}
    	
    }
    /**
     * Receive the DataIn and DataOut then decode the message to send it to execute it 
     * When this method is call, the first value will be the type of message, then the next following
     * values would be the data for the task.
     * @param dataIn: data in stream
     * @param dataOut: data out stream
     */
    public void decodeData(DataInputStream dataIn, DataOutputStream dataOut){
    	try {
			int headerNumber = dataIn.readInt();
			MessageType header = MessageType.values()[headerNumber];
			System.out.println(header.toString());
			Sound.playNote(Sound.PIANO, 600, 15);
			switch (header) {
			case MOVE:
				float[] move = new float[3];
				for (int i = 0; i < 2; i++) {
					move[i] = dataIn.readFloat();
				}
				System.out.println("Move " + move[0] + "," + move[1]);
				updateMessage(new Message(header, move));
				break;
			case FIX_POS:
				System.out.println("Fix pose");
				updateMessage(new Message(header, null));
				break;
			case STOP:
				System.out.println("Stop");
				updateMessage(new Message(header, null));
				break;
			case ROTATE:
				float[] rotate = new float[1];
				rotate[0] = dataIn.readFloat();
				System.out.println("Rotate " + rotate[0]);
				updateMessage(new Message(header, rotate));
				break;
			case ROTATE_TO:
				float[] rotateTo = new float[1];
				rotateTo[0] = dataIn.readFloat();
				System.out.println("Rotate to " + rotateTo[0]);
				updateMessage(new Message(header, rotateTo));						
				break;
			case TRAVEL:
				float[] travel = new float[1];
				travel[0] = dataIn.readFloat();
				System.out.println("Travel " + travel[0]);
				updateMessage(new Message(header, travel));
				break;
			case SET_POSE:
				float[] newPose = new float[3];
				for (int i = 0; i < 3; i++) {
					newPose[i] = dataIn.readFloat();
				}
				System.out.println("Set pose to " + newPose[0] + "," + newPose[1] + "," + 
						newPose[2]);
				updateMessage(new Message(header, newPose));
				break;
			case SEND_MAP:
				float[] whereToStop = new float[3];
				for (int i = 0; i < 3; i++) {
					whereToStop[i] = dataIn.readFloat();
				}
				System.out.println("Map");
				System.out.println("Mapping coordinates: " + whereToStop[0] + "," + whereToStop[1] + "," + 
						whereToStop[2]);
				updateMessage(new Message(header, whereToStop));
				break;
			case ECHO:
				System.out.println("Echo");
				float[] echo = new float[1];
				echo[0] = dataIn.readFloat();
				updateMessage(new Message(header, echo));
				break;
			case EXPLORE:
				System.out.println("Ping");
				updateMessage(new Message(header, null));
				break;
			case STDDEV:
				System.out.println("Standard Deviation");
				updateMessage(new Message(header, null));
				break;
			default:
				System.out.println("Invalid Message");
				break;
			}
		} catch (IOException e){
			System.out.print("DecodeData error");
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
		case ROTATE_TO:
			System.out.println("ROTATE TO");
			//((DifferentialPilot) navigator.getMoveController()).rotateTo(m.getData()[0]);
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
            ((DifferentialPilot) navigator.getMoveController()).setTravelSpeed(30);
			//locator.getScanner().rotate(m.getData()[2], false);
            navigator.goTo(m.getData()[0], m.getData()[1]);
            sendData(true, true);
            ((DifferentialPilot) navigator.getMoveController()).setTravelSpeed(70);
			break;
		case ECHO:
			System.out.println("ECHOING");
			sendEcho(m.getData()[0]);
			break;
		case EXPLORE:
			System.out.println("PINGING");
			sendPingAll();
			break;
		case STDDEV:
			System.out.println("PINGING");
			break;
		default:
			System.out.println("MESSAGE NOT IN THE LIST");
			break;
		}
	} 
	

	
}
