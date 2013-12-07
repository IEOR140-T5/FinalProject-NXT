package robot;

/**
 * Message between robot and PC
 */
public enum MessageType {
	GOTO, STOP, SET_POSE, FIX_POS, POS_UPDATE, 
	CRASH, ECHO, ROTATE, TRAVEL, ROTATE_TO, SCANNER_ROTATE, SEND_MAP, WALL,
	EXPLORE, STD_DEV, DISCONNECT, EXPLORE_RECEIVED, GRAB_BOMB
}
