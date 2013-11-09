package robot;

/**
 * The message to be communicated between the robot and the PC
 */
public class Message {

	private MessageType type;
	private float[] data;
	
	/**
	 * Constructor for Message
	 * @param mt An enum for various types of message used
	 * @param d An array of floats that represent the data stored in the message
	 */
	public Message(MessageType mt, float[] d) {
		type = mt;
		data = d;
	}
	
	/**
	 * @return the enum associated with this message
	 */
	public MessageType getType() {
		return type;
	}
	
	/**
	 * @return the array of floats stored within this message
	 */
	public float[] getData() {
		return data;
	}
}
