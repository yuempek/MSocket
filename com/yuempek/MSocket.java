package com.yuempek;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MSocket implements Runnable {
    private Socket socket;
    private ServerSocket serverSocket;

    private boolean running;
    private int bufferSize;
    
    private ExecutorService executer;
    
    private static int DEFAULT_SOCKET_SIZE = 1024;
    
    private ICallbackOnData callbackOnData;
    private ICallbackOnStopListen callbackOnStopListen;
    
    private boolean useFixedPacketSize = false;
    private int fixedPacketSize;
    
    private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN; //default byteOrder
    
    
    //Constructers
    //------------------
    
    public MSocket(String servername, int port) throws UnknownHostException, IOException {
        this(servername, port, DEFAULT_SOCKET_SIZE);
    }
    
    public MSocket(String servername, int port, int bufferSize) throws UnknownHostException, IOException {
        this(new Socket(servername, port), bufferSize);
    }
    
    public MSocket(Socket socket) throws SocketException {
        this(socket, DEFAULT_SOCKET_SIZE);
    }
    
    public MSocket(Socket socket, int bufferSize) throws SocketException {
        this.socket = socket;
        this.bufferSize = bufferSize;
    }
    
    
    //Run
    //------------------
    
    public void run(){
        
        MMessage message;
        
        InputStream inputStream = null;
        
        try {
            inputStream = this.socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        if (inputStream == null) return;
        
        this.running = true;
        
        MMessageInputStream messageInputStream = new MMessageInputStream(inputStream, this.byteOrder);
        
        while (this.running) {
            try {
                
                if (this.useFixedPacketSize){
                    message = messageInputStream.readMessage(this.fixedPacketSize);
                }else{
                    message = messageInputStream.readMessage();
                }
                                
                if (message == null) {
                    this.stoplisten();
                    return;
                }
                
                if (this.callbackOnData != null) 
                    this.callbackOnData.onData(message);
                
            } catch (SocketTimeoutException e){
                //try again
            } catch (IOException e){
                this.stoplisten();
                return;
            }
        }
    }
    
    
    
    //Callbacks
    //------------------
    
    public void setCallbackOnData (ICallbackOnData callbackOnData) {
        this.callbackOnData = callbackOnData;
    }
    
    public void setCallbackOnStopListen (ICallbackOnStopListen callbackOnStopListen) {
        this.callbackOnStopListen = callbackOnStopListen;
    }
    
    public ByteOrder getByteOrder(){
        return this.byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder){
        this.byteOrder = byteOrder;
    }
    
    public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

    //Listen Actions
    //------------------
    public void startlisten(){
        this.executer = Executors.newSingleThreadExecutor();
        this.executer.submit(this);
    }
    
    public void stoplisten(){
        if (!this.running) return;
        this.running = false;
        
        try {
            this.socket.shutdownInput();
        } catch (Exception e){}
        
        try {
            this.socket.close();
        } catch (Exception e){}
        
        try {
            if (this.serverSocket != null)
            	this.serverSocket.close();
        } catch (Exception e){}
        
        if (this.callbackOnStopListen != null)
            this.callbackOnStopListen.onStopListen();
        
        this.executer.shutdownNow();
    }
    
    
    //Send
    //----------------
    
    public void send(byte[] msg) throws IOException {
        this.send(msg, 0, msg.length);
    }
    
    public void send(byte[] msg, int length) throws IOException {
        this.send(msg, 0, length);
    }
    
    public void send(MMessage message) throws IOException {
        byte[] messageBytes = message.getMessage();
        int messageSize = message.getSizeOfMessage();
        int messageHeaderSize = message.getSizeOfMessageHeader();
        
        if(this.useFixedPacketSize){
            messageBytes = Arrays.copyOf(messageBytes, this.fixedPacketSize + messageHeaderSize);
            this.send(messageBytes, 0, this.fixedPacketSize + messageHeaderSize);
        }else{
            this.send(messageBytes, 0, messageSize);
        }
    }
    
    public void send(byte[] msg, int offset, int length) throws IOException {
        OutputStream outputStream = this.socket.getOutputStream();
        
        outputStream.write(msg, offset, length);
        outputStream.flush();
    }
    
    
    public void onDestroy(){
        this.running = false;
    }
    
    public boolean isRunning(){
        return this.running;
    }
    
    public int getBufferSize(){
        return bufferSize;
    }
    
    public void setFixedPacketSize(int packetSizeAsByte){
        this.fixedPacketSize = packetSizeAsByte;
        this.useFixedPacketSize = true;
    }
    
}
