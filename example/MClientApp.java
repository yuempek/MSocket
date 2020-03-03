package example;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import com.yuempek.ICallbackOnData;
import com.yuempek.ICallbackOnStopListen;
import com.yuempek.MMessage;
import com.yuempek.MSocket;
import com.yuempek.MSocketManager;

public class MClientApp implements ICallbackOnData, ICallbackOnStopListen {
    
    public MClientApp() throws UnknownHostException, IOException {
    	System.out.println("Client: Client is starting.");
    	
    	MSocket socket;
        String servername = "localhost";
        int port = 60006;
        
        System.out.println("Client: Client is connecting.");
        try {
            socket = new MSocketManager().connectToServer(servername, port); //Create Socket Manager & Connect to Server 
        } catch (Exception e){
        	return;
        }
        
        socket.setCallbackOnData(this);					//set onData callback
        socket.setCallbackOnStopListen(this);			//set onStop callback
        socket.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        socket.startlisten();							//start listen
        System.out.println("Client: Client connected.");
    	
        
        //Scenario
        
        // 1.Message Send
        int messageId = 0;
        String messageText = "test message";
        byte[] messageData = messageText.getBytes();
        int messageLength = messageData.length;
        MMessage message = new MMessage(messageId, messageLength, messageData, ByteOrder.LITTLE_ENDIAN);
        
        try {
        	System.out.println("Client: Client is sending message: \"" + messageText +"\"");
            socket.send(message);
        } catch (Exception exception) {
        	System.out.println("Client: packet couldn't sent.");
        	return;
        }
                
        // 2.Connection Stop
        socket.stoplisten();
    }
    
    
    @Override
    public void onData(MMessage message) {
        System.out.println("Client: Data :" + new String(message.getMessageData()));
    }
    
    
    @Override
    public void onStopListen() {
        System.out.println("Client: Connection closed.");
    }
    

}
