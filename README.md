Final Project - NXT
===================

Milestone 4A:

    Mission Control -- Set up the commands setPose, goTo, and stop. Also, set up a MouseListener where the mouse
                       coordinates are relayed to the class based on position. Another task of this Milestone was to
                       set up the GUI that will be used throughout this entire project. 
                       
    Classes -- PC SIDE: CommListener, GNC, GridControlCommunicator, MessageType, OffScreenDrawing.
               NXT SIDE: Locator, Scanner, Message, MessageType, Controller, Communicator, CommListener
               
    Data Flow for goTo() Call:
    
            1. GUI is built.
            2. User Moves Mouse and Picks a Position to goTo.
            3. PC Relays type of message (enum GOTO) via Bluetooth.
            4. MessageType arrives at NXT Side.
            5. NXT Decodes the MessageType to determine which ENUM is being implemented.
            6. NXT Tells Robot to carry out assigned ENUM.
            7. After performing action, NXT uses Locator class to determine current Location.
            8. Current Location is Relayed back to PC.
            
    
    Data Flow for stop() Call:
    
            1. GUI is built.
            2. While Rover is in motion, user clicks the STOP JButton.
            3. Our UpdateMessage() method is called to Relay the current MessageType.
            4. PC Class Controller receives the UpdatedMessage, if the message == STOP, 
            5. We do three things: clear our inbox Message Arraylist, Stop Navigator, and call clearPath on navigator.
            6. Finally, the Locator class relays current approximated Location.
