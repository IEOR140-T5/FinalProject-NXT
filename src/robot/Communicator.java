package robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * Communicator takes care of the communication between the NXT and the PC part
 * by using bluetooth connection
 * Based on the Original Communicator by professor Glassy
 */
public class Communicator {
	private BTConnection btc;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private Reader reader;
	private Controller controller;

	/**
	 * Start the communicator
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
	 * Start connecting and wait for the bluetooth to finish connecting
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
	}

	/**
	 * Close on exit
	 * 
	 * @throws IOException
	 */
	public void exit() throws IOException {
		dataIn.close();
		dataOut.close();
		btc.close();
	}

	/**
	 * Read the inputsteam and execute everything
	 * 
	 * @author Roger Glassey
	 */
	class Reader extends Thread {

		int count = 0;
		boolean isRunning = false;

		/**
		 * While running, the controller will take the message from the PC, put it in the queue and
		 * execute it.
		 */
		public void run() {
			System.out.println("Ready to go ...");
			isRunning = true;
			while (isRunning) {
				// start transmitting the message to the Controller
				controller.decodeData(dataIn, dataOut);
			}
		}
	}
}