package robot;

/**
 * All of the messages used to communicate between PC and NXT
 */
public class Message {

	private MessageType type;
	private float[] data;
	
	/**
	 * Constructor for Message
	 * @param mt: message type, supposed to be an enum
	 * @param d: array of data
	 */
	public Message(MessageType mt, float[] d) {
		type = mt;
		data = d;
	}
	
	/**
	 * @return return the name of the Action
	 */
	public MessageType getType() {
		return type;
	}
	
	/**
	 * @return return the data depends on what kind of action
	 */
	public float[] getData() {
		return data;
	}
}
