package robot;

/**
 * The various message types that can be sent between the robot and the computer.
 */
public enum MessageType {
	MOVE, STOP, SET_POSE, FIX_POS, POS_UPDATE, 
	CRASH, PING, ROTATE, TRAVEL, MAP, CAPTURE, OBSTACLE
}
