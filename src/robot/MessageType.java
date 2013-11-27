package robot;

/**
 * Message between robot and PC
 */
public enum MessageType {
	MOVE, STOP, SET_POSE, FIX_POS, POS_UPDATE, 
	CRASH, ECHO, ROTATE, TRAVEL, ROTATE_TO, SCANNER_ROTATE, SEND_MAP, WALL, EXPLORE
}
