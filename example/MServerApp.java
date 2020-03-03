package example;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import com.yuempek.ICallbackOnData;
import com.yuempek.ICallbackOnStopListen;
import com.yuempek.MMessage;
import com.yuempek.MSocket;
import com.yuempek.MSocketManager;

public class MServerApp implements ICallbackOnData, //!important
								   ICallbackOnStopListen //!important
{
	MSocket socket;
	MSocketManager socketManager;
    
    public MServerApp() throws UnknownHostException, IOException {
        this.startServer();
    }
    
    public void startServer(){
    	System.out.println("Server: Server is starting.");
        int port = 60006;
        this.socketManager = new MSocketManager(); //Create Socket Manager
        
        try {
            socket  = socketManager.startAsServer(port); //Start Server Socket
        } catch (Exception e){ 
        	return;
        }
        
        System.out.println("Server: Socket created.");
        socket.setCallbackOnData(this); 				//set onData callback
        socket.setCallbackOnStopListen(this);			//set onStop callback
        socket.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        socket.startlisten(); 							//start listen
        System.out.println("Server: Socket is listening.");
    }
    
    
    @Override
    public void onData(MMessage message) {
        System.out.println("Server: Received Data : \"" +new String(message.getMessageData()) + "\"");
        socket.stoplisten();
    }
    
    
    @Override
    public void onStopListen() {
        System.out.println("Server: Connection closed.");
        this.startServer();
    }
    
}
