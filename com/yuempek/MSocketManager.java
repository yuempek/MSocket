package com.yuempek;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MSocketManager {
    private MSocket socket = null;
    private ServerSocket serverSocket = null;
    
    public MSocketManager(){}
    
    public MSocket startAsServer(int port) throws SocketException, IOException {
        if (this.serverSocket == null) {
            try {
                this.serverSocket = new ServerSocket(port);
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }
        
        this.socket = new MSocket(this.serverSocket.accept());
        this.socket.setServerSocket(this.serverSocket);
        return this.socket;
    }
    
    public MSocket connectToServer(String servername, int port) throws UnknownHostException, IOException {
        this.socket = new MSocket(servername, port);
        return this.socket;
    }
    
    public MSocket getSocket(){
        return this.socket;
    }
}
