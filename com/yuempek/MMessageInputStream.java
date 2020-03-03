package com.yuempek;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class MMessageInputStream{
    private InputStream inputStream;
    private DataInputStream dataInputStream;
    private ByteOrder byteOrder;
    
    public MMessageInputStream(InputStream inputStream, ByteOrder byteOrder){
        this.inputStream = inputStream;
        this.dataInputStream = new DataInputStream(inputStream);
        this.byteOrder = byteOrder;
    }
    
    public MMessage readMessage() throws IOException {
        int messageId = this.getMessageId();
        int messageLength = this.getMessageLength();
        
        MMessage message = new MMessage(messageId, messageLength, this.byteOrder);
        
        byte[] messageBuffer = message.getMessageData();
        
        if (messageBuffer == null || messageBuffer.length < messageLength) return null;
        
        int size = this.inputStream.read(messageBuffer, 0, messageLength);
        
        if (size != messageLength) return null;
        
        return message;
    }
    
    public MMessage readMessage(int fixedPacketSize) throws IOException {
        return this.readFixedSizeMessage(fixedPacketSize);
    }
    
    private MMessage readFixedSizeMessage(int fixedPacketSize) throws IOException {
        int messageId = this.getMessageId();
        int messageLength = this.getMessageLength();
        
        byte[] messageBuffer = new byte[fixedPacketSize];
        
        // if (messageBuffer == null || messageBuffer.length < messageLength) return null;
        
        int size = this.inputStream.read(messageBuffer, 0, fixedPacketSize);
        
        if (size != fixedPacketSize) return null;
        
        MMessage message = new MMessage(messageId, messageLength, messageBuffer, this.byteOrder);
        
        return message;
    }
    
    private int getMessageId() throws IOException {
        return readInt();
    }
    
    private int getMessageLength() throws IOException {
        return readInt();
    }
    
    private int readInt() throws IOException {
        byte[] bytes = new byte[Integer.SIZE/8];
        this.dataInputStream.read(bytes);
        return ByteBuffer.wrap(bytes).order(this.byteOrder).getInt();
    }
}
