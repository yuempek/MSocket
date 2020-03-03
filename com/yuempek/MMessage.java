package com.yuempek;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MMessage {
    private int messageId;
    private int messageLength;
    
    private byte[] messageData;
    
    private ByteOrder byteOrder;
    
    public MMessage(int messageId, int messageLength, ByteOrder byteOrder){
        this(messageId, messageLength, new byte[messageLength], byteOrder);
    }
    
    public MMessage(int messageId, int messageLength, byte[] messageData, ByteOrder byteOrder){
        this.setMessageId(messageId);
        this.setMessageLength(messageLength);
        this.setMessageData(messageData);
        this.setByteOrder(byteOrder);
    }
    
    public int getMessageId(){
        return messageId;
    }

    public void setMessageId(int messageId){
        this.messageId = messageId;
    }

    public int getMessageLength(){
        return messageLength;
    }

    public void setMessageLength(int messageLength){
        this.messageLength = messageLength;
    }

    public byte[] getMessageData(){
        return messageData;
    }

    public void setMessageData(byte[] messageData){
        this.messageData = messageData;
    }

    public ByteOrder getByteOrder(){
        return this.byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder){
        this.byteOrder = byteOrder;
    }

    public int getSizeOfMessageId(){
        return Integer.SIZE/8;
    }

    public int getSizeOfMessageLength(){
        return Integer.SIZE/8;
    }

    public int getSizeOfMessageData(){
        return this.messageData.length;
    }

    public int getSizeOfMessageHeader(){
        return getSizeOfMessageId() + getSizeOfMessageLength();
    }

    public int getSizeOfMessage(){
        return getSizeOfMessageHeader() + getMessageLength();
    }

    public byte[] getMessage(){
        int totalMessageSize = getSizeOfMessage();
        ByteBuffer buffer = ByteBuffer.allocate(totalMessageSize)
                                      .order(this.getByteOrder());
        
        buffer.putInt(this.getMessageId());
        buffer.putInt(this.getMessageLength());
        buffer.put(this.getMessageData(), 0, this.getMessageLength());
        
        return buffer.array();
    }
}
