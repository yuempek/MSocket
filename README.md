# MSocket
Event base tcp socket on Java. It carries MMessage packets on tcp stream. When an MMessage arrived, it sends the message to your onData method. 

## Server : 
```java
int port = 2222;

MSocket socket = new MSocketManager().startAsServer(port);
socket.setCallbackOnData(this);
socket.setCallbackOnStopListen(this);
socket.startlisten();

@Override
public void onData(MMessage message) {
    // logic of comming message
}

@Override
public void onStopListen() {
    // logic of comminication stop
}
```

## Client :
```java
String servername = "localhost";
int port = 2222;

MSocket socket = new MSocketManager().connectToServer(servername, port);
socket.setCallbackOnData(this);
socket.setCallbackOnStopListen(this);
socket.startlisten();

sendSampleMessage(socket);


public void sendSampleMessage(MSocket socket){
    int messageId = 0;
    String messageText   = "test message";
    byte[] messageData   = messageText.getBytes();
    int    messageLength = messageData.length;
    
    MMessage message = new MMessage(messageId, messageLength, messageData, ByteOrder.LITTLE_ENDIAN);
    socket.send(message);
}

@Override
public void onData(MMessage message) {
    // logic of comming message
}

@Override
public void onStopListen() {
    // logic of comminication stop
}
```
