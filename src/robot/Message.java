package robot;

/**
 * Main message class used to communicate between PC and NXT
 */
public class Message {

	private MessageType type;
	private float[] data;
	
	/**
	 * Constructor for Message
	 * @param mt: message type, supposed to be an enum
	 * @param d: array of data
	 */
	public Message(MessageType inputMT, float[] inputD) {
		type = inputMT;
		data = inputD;
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
