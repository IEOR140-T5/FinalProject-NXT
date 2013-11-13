package robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * The Communicator connects to the PC through a bluetooth channel and
 * receives/sends messages.
 */
public class Communicator {
	private BTConnection btc;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private Reader reader;
	private Controller controller;

	/**
	 * Creates a BTCommunicator object and connects it to the computer, then
	 * sets up the data streams and such.
	 */
	public Communicator() {
		reader = new Reader();
		connect();
		try {
			dataIn = btc.openDataInputStream();
			dataOut = btc.openDataOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		reader.start();
		System.out.println("Data stream opened.");
	}

	/**
	 * Sets a controller to control messages from this communicator
	 * @param c - a robot controller
	 */
	public void setController(Controller c) {
		controller = c;
	}
	
	/**
	 * Establishes a bluetooth connection with the computer.
	 */
	public void connect() {
		LCD.drawString("connecting...", 0, 0);
		btc = Bluetooth.waitForConnection();
		LCD.clear();
		LCD.drawString("connected", 0, 0);
		LCD.refresh();
		Sound.beepSequence();
	}

	/**
	 * Sends a message to the computer specified by header, x, and y
	 * 
	 * @param Message
	 *            - the message to be sent
	 * @throws IOException
	 */
	public void send(Message m) throws IOException {
		System.out.println(m.getType().ordinal());
		dataOut.writeInt(m.getType().ordinal());
		if (m.getData() != null) {
			for (int i = 0; i < m.getData().length; i++) {
				System.out.println(m.getData()[i]);
				dataOut.writeFloat(m.getData()[i]);
				dataOut.flush();
			}
		}
		Sound.playNote(Sound.PIANO, 444, 12);
		LCD.drawString("Send", 0, 0);
	}

	/**
	 * Closes everything
	 * 
	 * @throws IOException
	 */
	public void exit() throws IOException {
		dataIn.close();
		dataOut.close();
		btc.close();
	}

	/**
	 * reads the data input stream, and calls DrawRobotPath() and DrawObstacle()
	 * uses OffScreenDrawing, dataIn
	 * 
	 * @author Roger Glassey
	 */
	class Reader extends Thread {

		int count = 0;
		boolean isRunning = false;

		/**
		 * Runs the reader and takes in readings that the robot sends. The
		 * robot's communications contain two parts: a MessageType that
		 * indicates what the message means the robot to do, and an array of
		 * floats that represent the data included in that message.
		 * 
		 * The size of the float array depends on the message type, so this
		 * method must parse the message type to construct a message object with
		 * an accurate amount of data.
		 * 
		 */
		public void run() {
			System.out.println(" reader started GridControlComm1 ");
			isRunning = true;
			Sound.playNote(Sound.PIANO, 500, 50);
			while (isRunning) {
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
						controller.updateMessage(new Message(header, move));
						break;
					case FIX_POS:
						System.out.println("Fix pose");
						controller.updateMessage(new Message(header, null));
						break;
					case STOP:
						System.out.println("Staph");
						controller.updateMessage(new Message(header, null));
						break;
					case ROTATE:
						float[] rotate = new float[1];
						rotate[0] = dataIn.readFloat();
						System.out.println("Rotate " + rotate[0]);
						controller.updateMessage(new Message(header, rotate));
						break;
					case TRAVEL:
						float[] travel = new float[1];
						travel[0] = dataIn.readFloat();
						System.out.println("Travel " + travel[0]);
						controller.updateMessage(new Message(header, travel));
						break;
					case SET_POSE:
						float[] newPose = new float[3];
						for (int i = 0; i < 3; i++) {
							newPose[i] = dataIn.readFloat();
						}
						System.out.println("Set pose to " + newPose[0] + "," + newPose[1] + "," + 
								newPose[2]);
						controller.updateMessage(new Message(header, newPose));
						break;
					default:
						System.out.println("What the heck is this message?");
						break;
					}
				} catch (IOException e) {
					System.out.println("Read Exception in GridControlComm");
					count++;
				}
			}
		}
	}
}