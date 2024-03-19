package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private Message[] messages;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private Integer amountOfMessages;


    public Client(int amountOfMessages){

            this.amountOfMessages = amountOfMessages;
            this.messages = new Message[amountOfMessages];
            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < amountOfMessages; i++) {
                messages[i] = new Message(i, "message number" + i);
            }

    }
    public void initlizeConnection(Socket socket){
        try{
            this.socket = socket;
            this.outStream = new ObjectOutputStream(socket.getOutputStream());
            this.inStream = new ObjectInputStream(socket.getInputStream());

        }catch (IOException e){
            closeConnections();
            e.printStackTrace();
        }

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

    public void transmit(){

        try{
            if(!"ready".equals((String) inStream.readObject())) closeConnections();
            System.out.println("Starting transmision");
            outStream.writeObject(this.amountOfMessages);
            System.out.println("amount of messages sent");
            if(!"ready for messages".equals((String) inStream.readObject())) closeConnections();
            System.out.println("ready for messages recived");
            for(Message message: messages){

                outStream.writeObject(message);
            }
        }catch(IOException | ClassNotFoundException e){

            closeConnections();
            e.printStackTrace();
        }
        closeConnections();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(Integer.parseInt(args[0]));
        client.initlizeConnection(new Socket("localhost", 1234));
        client.transmit();
    }


}
