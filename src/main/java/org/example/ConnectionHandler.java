package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable{

    private Socket socket;
    private Server server;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    public ConnectionHandler(Socket socket, Server server){
        try{
            this.server = server;
            this.socket = socket;
            this.outStream = new ObjectOutputStream(socket.getOutputStream());
            this.inStream = new ObjectInputStream(socket.getInputStream());
        }catch(IOException e){
            closeConnections();
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        int amountOfMessages;
        Message[] receivedMessages;
        try {

            outStream.writeObject("ready");
            System.out.println("SERVER: ready");

            amountOfMessages = (Integer) inStream.readObject();
            System.out.println("CLIENT: " + amountOfMessages + " messages will be sent");
            receivedMessages = new Message[amountOfMessages];
            System.out.println("SERVER: ready for " + amountOfMessages + "to be sent");
            outStream.writeObject("ready for messages");

            for(int i = 0; i < amountOfMessages; i++){
                receivedMessages[i] = (Message) inStream.readObject();
                //receivedMessages[i].printMessage();
            }
            System.out.println("SERVER: all messages recived properly");

            for(Message message: receivedMessages){
                this.server.AppendNewMessages(message);
            }

        } catch (IOException | ClassNotFoundException e) {
            closeConnections();
            e.printStackTrace();
        }
        closeConnections();

    }

    private void closeConnections(){
        System.out.println("closing connections");
        try{
            if(this.inStream != null) this.inStream.close();
            if(this.outStream != null) this.outStream.close();
            if(this.socket != null && !this.socket.isClosed()) this.socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
