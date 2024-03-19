package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private volatile ArrayList<Message> receivedMessages;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        this.receivedMessages = new ArrayList<Message>();
    }
    public synchronized void AppendNewMessages(Message newMessage){
        this.receivedMessages.add(newMessage);
    }
    public void printMessages(){
        for(Message message: receivedMessages){
            message.printMessage();
        }
    }
    public void closeServer(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startServer(){
        try {
            Socket socket = serverSocket.accept();
            System.out.println("New client has connected");
            ConnectionHandler connectionHandler = new ConnectionHandler(socket,this);
            Thread thread = new Thread(connectionHandler);
            thread.start();
            thread.join();

        } catch (IOException e ) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*while(!serverSocket.isClosed()){
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client has connected");
                ConnectionHandler connectionHandler = new ConnectionHandler(socket,this);
                Thread thread = new Thread(connectionHandler);
                thread.start();
            } catch (IOException e ) {
                throw new RuntimeException(e);
            }
        }*/
    }

    private Thread serverStarter(){
        Server host = this;
        return new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("SERVER: ON");
                while(!serverSocket.isClosed()){
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("New client has connected");
                        ConnectionHandler connectionHandler = new ConnectionHandler(socket,host);
                        Thread thread = new Thread(connectionHandler);
                        thread.start();
                    } catch (IOException e ) {
                        return;
                    }
                }

            }
        });
    }


    public static void main(String args[]) throws IOException {
        Server server = new Server(new ServerSocket(1234));
        //server.startServer2(server);
        Thread acceptingThread = server.serverStarter();
        acceptingThread.start();
        Scanner scanner = new Scanner(System.in);
        Boolean cont = true;
        while(cont){
            String command = scanner.nextLine();
            switch (command){
                case "-q":
                    acceptingThread.interrupt();
                    server.closeServer();
                    cont = false;
                    System.out.println("SERVER: OFF");
                    break;
                case "-p":
                    server.printMessages();
                    break;
            }
        }
        //server.printMessages();



    }



}
