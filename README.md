Final Project - NXT
===================

Milestone 4A:

**Mission Control**: Set up the commands setPose, goTo, and stop. Also, set up a MouseListener where the mouse 
coordinates are relayed to the class based on position. Another task of this Milestone was to 
set up the GUI that will be used throughout this entire project. 
                       
**Classes**: 
PC SIDE: CommListener, GNC, GridControlCommunicator, MessageType, OffScreenDrawing.
NXT SIDE: Locator, Scanner, Message, MessageType, Controller, Communicator, CommListener
               
**Data Flow for goTo()/STOP Call**:
    
1. GUI is built. (CommListener class)
2. GUI adds GoToButtonActionListener().
3. GoToButtonActionListener() calls sendMove().
4. sendMove() parses the input text fields to get X and Y coordinates according to MouseLocation. 
5. sendMove() uses a GridControllerNavigator object to call controller.sendDestination(x,y).
6. GridControllerNav uses two DataStream Objects (One Input and One Output).
7. In sendDestination:  dataOut.writeInt(MessageType.MOVE.ordinal());
                        dataOut.flush();
                        dataOut.writeFloat(x);
                        dataOut.flush();
                        dataOut.writeFloat(y);
                        dataOut.flush();
3. PC Relays type of message above (enum GOTO) via Bluetooth Connection object btc.
4. MessageType arrives at NXT Side via Communicator class.
5. Communicator class uses the Multi Threading Method run() to decode MessageType().
6. run() uses a switch statement to determine MessageType() and calls updateMessage() accordingly.
7. Control is returned the the Controller class and updateMessage runs to determine what kind of message is passed.
8. If Message is STOP, empty entire ArrayList<Message>. navigator.stop(). And navigator.clearPath().
7. After performing action, NXT uses Locator class to determine current Location.
8. Current Location is Relayed back to PC.
            
    
**Data Flow for stop() Call**:
    
1. GUI is built.
2. While Rover is in motion, user clicks the STOP JButton.
3. Our UpdateMessage() method is called to Relay the current MessageType.
4. PC Class Controller receives the UpdatedMessage, if the message == STOP, 
5. We do three things: clear our inbox Message Arraylist, Stop Navigator, and call clearPath on navigator.
6. Finally, the Locator class relays current approximated Location.
