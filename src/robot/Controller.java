package robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
	private int _obstacleDistance = 240;
	private Detector detector;

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
	 * Constructor that takes in Navigator and Locator and Detector
	 * @param nav
	 * @param loc
	 * @param td
	 */
	public Controller(Navigator nav, Locator loc, Detector td) {
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
		if (message.getType() == MessageType.STOP) {
			queue.clear();
			navigator.stop();
			navigator.clearPath();
		}
		else {
			queue.add(message);
		}
	}

	/**
	 * Check the queue and execute everything
	 */
	public void go() {
		Message currentMessage = new Message(MessageType.STOP, new float[1]);
		while (true) {
			while (!queue.isEmpty()) {
				currentMessage = queue.remove(0);
				execute(currentMessage);
			}
		}
	}
	
    public void touchSensorPressed(boolean isLeftTouched, boolean isRightTouched) {
    	int distance = 10;
    	int angleToWall = 20;
        if (isLeftTouched) {
        	sendCrash(distance, -angleToWall);
        }
        else {
        	sendCrash(distance, angleToWall);
        }
        navigator.stop();
        navigator.clearPath();
        sendPose();
    }
    
    /**
     * Returns a poseArray of the current pose's x, y, and heading.
     * @return
     */
    private float[] poseArray() {
    	VariancePose currPose = (VariancePose) navigator.getPoseProvider().getPose();
    	float[] poseArray = new float[3];
		poseArray[0] = currPose.getX();
		poseArray[1] = currPose.getY();
		poseArray[2] = currPose.getHeading();
		return poseArray;
    }
	
	/**
	 * Send the pose back to the PC to update
	 */
	private void sendPose() {
		try {
			communicator.send(new Message(MessageType.POS_UPDATE, poseArray()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send the message back to PC and let it hand this case
	 * either prints out the message to let the user knows
	 * or do something else
	 */
    private void sendCrash(int distance, int angleToWall) {
        try {
        	communicator.send(new Message(MessageType.CRASH, wallPoint(distance, angleToWall)));
        }
        catch (IOException e) {
            System.out.println("Exception thrown.");
        }
    }
	
    /**
     * Returns a wallPointArray of wallPoints x, y, respectively, that are sent to the GUI to map
     * the walls and bomb. 
     * Is used by sendCrash(), sendWall(), and sendExplore().
     * @param obstacleDistance
     * @param angle
     * @return
     */
    private float[] wallPoint(int obstacleDistance, int angle) {
    	float[] wallPointArray = new float[2];
        Point wallPoint =
        		navigator.getPoseProvider().getPose().pointAt(obstacleDistance, 
        				angle + navigator.getPoseProvider().getPose().getHeading());
        wallPointArray[0] = wallPoint.x;
        wallPointArray[1] = wallPoint.y;
		return wallPointArray;
    }
    
	/**
	 * Send the wall location back to PC
	 */
    private void sendWall(int obstacleDistance, int angle) {
        try {
        	communicator.send(new Message(MessageType.WALL, wallPoint(obstacleDistance, angle)));
        } 
        catch (IOException e) {
            System.out.println("Exception at SENDWALL");
        }
    }
    
    /**
	 * 
	 */
    private void sendExplore(int obstacleDistance, int angle) {
        try {
        	communicator.send(new Message(MessageType.EXPLORE_RECEIVED, wallPoint(obstacleDistance, angle)));
        } 
        catch (IOException e) {
            System.out.println("Exception at SENDWALL");
        }
    }
	
	/**
	 * Send Pose back to PC, depending on what type it is.
	 * This method is used for both sending pose back or sending pose and wall location back
	 * @param sendPose: send Pose true/false
	 * @param sendWall: send wall location if set true
	 */
    private void sendData(boolean sendPose, boolean sendWallBoolean) {
        while (navigator.isMoving() || navigator.getMoveController().isMoving()) {
            int obstaceDistance;
            // headAngle = getTachoCount()
            int currentHeadAngle = locator.getScanner().getHeadAngle();
            
           sendPose();
            // getEchoDistance = ultrasonic.getDistance()
            obstaceDistance = locator.getScanner().getEchoDistance();
            if (sendWallBoolean && (obstaceDistance < _obstacleDistance)) {
                sendWall(obstaceDistance, currentHeadAngle);
            }
        
        }
        sendPose();    
    }	
    
    /**
     * The robot sends out a ping used to map a single point on the GUI. 
     * @param angle 
     */
    private void sendEcho(float angle) {
    	locator.getScanner().rotateHeadTo(angle);
    	int obstacleDistance = locator.getScanner().getEchoDistance(angle);
    	sendWall(obstacleDistance, (int) angle);
    }
    
    /**
     * Sends a ping to the surrounding area by a given exploreAngle from the user.
     */
    private void sendExplore(float exploreAngle) {
    	locator.getScanner().rotateHeadTo(exploreAngle);
        Delay.msDelay(200);
        locator.getScanner().rotateTo((int)-exploreAngle, true);
    	while (locator.getScanner().getMotor().isMoving()) {
    		int obstaceDistance;
    		int currentHeadAngle = locator.getScanner().getHeadAngle();
            obstaceDistance = locator.getScanner().getEchoDistance();
            
            if (obstaceDistance < _obstacleDistance) {
                sendExplore(obstaceDistance, currentHeadAngle);
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
    public void decodeData(DataInputStream dataIn, DataOutputStream dataOut) {
    	try {
			int headerNumber = dataIn.readInt();
			MessageType header = MessageType.values()[headerNumber];
			System.out.println(header.toString());
			Sound.playNote(Sound.PIANO, 600, 15);
			switch (header) {
			case GOTO:
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
				System.out.println("Mapping coordinates: " + whereToStop[0] + 
						"," + whereToStop[1] + "," + whereToStop[2]);
				updateMessage(new Message(header, whereToStop));
				break;
			case ECHO:
				System.out.println("Echo");
				float[] echo = new float[1];
				echo[0] = dataIn.readFloat();
				updateMessage(new Message(header, echo));
				break;
			case EXPLORE:
				System.out.println("Explore");
				float[] exploreAngle = new float[1];
                exploreAngle[0] = dataIn.readFloat();
                updateMessage(new Message(header, exploreAngle));
				break;
			case STD_DEV:
				System.out.println("Standard Deviation");
				updateMessage(new Message(header, null));
				break;
			default:
				System.out.println("Invalid Message");
				break;
			}
		} 
    	catch (IOException e){
			System.out.print("DecodeData error");
			try {
				communicator.exit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }
    
	/**
	 * Parses the given message and acts on it accordingly.
	 * STOP: stop the robot
	 * MOVE: calls the navigator to move to a given x,y
	 * FIX_POSE: fixes the position based on the bearings and echo distance
	 * SET_POSE: updates the locator's and navigator's pose to a given pose
	 * @param m - the message that represents which action to be executed
	 */
	private void execute(Message m) {
		Sound.playNote(Sound.PIANO, 450, 15);
		switch(m.getType()) {
		case STOP:
			navigator.stop();
			sendPose();
			break;
		case GOTO:
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
             float currentHeading = navigator.getPoseProvider().getPose().getHeading();
             float desiredHeading = m.getData()[0];
             float adjustedHeading = (desiredHeading - currentHeading);
			((DifferentialPilot) navigator.getMoveController()).rotate(adjustedHeading);
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
			System.out.println("MAPPING");
            locator.getScanner().rotateHeadTo(m.getData()[2]);
            navigator.goTo(m.getData()[0], m.getData()[1]);
            sendData(true, true);
			break;
		case ECHO:
			System.out.println("ECHOING");
			sendEcho(m.getData()[0]);
			break;
		case EXPLORE:
			System.out.println("EXPLORE");
			sendExplore(m.getData()[0]);
			break;
		case STD_DEV:
			System.out.println("STANDARD DEVIATION");
			//sendStdDev();
			break;
		default:
			System.out.println("MESSAGE NOT IN THE LIST");
			break;
		}
	} 
	

	
}
