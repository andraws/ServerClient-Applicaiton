package ServerClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {

  // my own output class to get read messages
  private Output out;
  // uniqueId counter for threads
  private int uniqueId = 0;
  // an ArrayList to keep the list of the Client
  private ArrayList<ServerThread> threadList;
  // to display time
  private SimpleDateFormat sdf;
  // the port number to listen for connection
  private int port;
  // notification
  private String notif = " *** ";
  // it is running
  private Boolean running = null;

  // constructor that receive the port number and output
  public Server(int port, Output output) {
    // the output object to read messages
    this.out = output;
    // the port
    this.port = port;
    // to display hh:mm am/pm
    sdf = new SimpleDateFormat("hh:mm a");
    // an ArrayList to keep the list of the Client
    threadList = new ArrayList<ServerThread>();
  }

  public void start() {
    running = true;
    // create socket server and wait for connection requests
    try {
      // the socket used by the server
      ServerSocket serverSocket = new ServerSocket(port);
      out.setInput("socketCreated");

      // infinite loop to wait for connections ( till server is active )
      while (running) {
        display("Server waiting for Clients on port " + port + ".");

        // accept connection if requested from client
        Socket socket = serverSocket.accept();

        // if client is connected, create its thread
        ServerThread t = new ServerThread(socket);
        // add this client to arraylist
        threadList.add(t);
        t.start();
      }
    } catch (IOException e) {
      String msg = "Exception on new ServerSocket: " + e + "\n";
      display(msg);
    }
  }

  // Display an event to the console and Output class, used by ScreenController (will refactor soon)
  private void display(String msg) {
    String time = "[" + sdf.format(new Date()) + "]" + " " + msg;
    out.setInput(time);
    System.out.println(time);
  }

  private synchronized void directMessage(ClientMessage msg) {
    String user = msg.getUser();
    String sender = user.substring(0, user.indexOf("@"));
    String receiver = user.substring(user.indexOf("@") + 1);

    String timeStamp = sdf.format(new Date());

    for (ServerThread ct : threadList) {
      String check = ct.getUsername();
      if (check.equals(receiver)) {
        ServerMessage  sMsg= new ServerMessage(ServerMessage.DIRECT,sender,msg.getMessage());
        sMsg.updateTimeStamp(timeStamp);
        ct.writeMsg(sMsg);
      }
    }
  }

  // to broadcast a message to all Clients
  private synchronized void broadcast(ServerMessage msg) {
    // add timestamp to the message
    String time = sdf.format(new Date());

    ServerMessage broadCastMsg = msg;
    broadCastMsg.updateTimeStamp(time);

    for(int i = 0; i < threadList.size(); i++){
      ServerThread ct = threadList.get(i);
      // try to write to the Client if it fails remove it from the list
      if (!ct.writeMsg(broadCastMsg)) {
        threadList.remove(i);
        display("Disconnected Client " + ct.username + " removed from list.");
      }
    }
  }

  synchronized void remove(int id) {

    String disconnectedClient = "";
    // scan the array list until we found the Id
    for (int i = 0; i < threadList.size(); ++i) {
      ServerThread ct = threadList.get(i);
      // if found remove it
      if (ct.id == id) {
        disconnectedClient = ct.getUsername();
        threadList.remove(i);
        break;
      }
    }
    ServerMessage discMsg = new ServerMessage(ServerMessage.WHOLEFT, disconnectedClient, disconnectedClient + " left the server");
    broadcast(discMsg);
  }

  private String createUsersString() {
    String userList = "";
    if (threadList.size() > 0) {
      userList = "";
      for (int i = 0; i < threadList.size(); ++i) {
        ServerThread ct = threadList.get(i);
        userList += ct.username + " ";
      }

      userList.substring(0, userList.length() - 1); // remove last space
    }
    return userList;
  }

  // One instance of this thread will run for each client
  class ServerThread extends Thread {
    // the socket to get messages from client
    Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    // unique id (easier for disconnecting)
    int id;
    // the Username of the Client
    String username;
    // message object to receive message and its type
    ClientMessage cm;
    // SimpleDateFormat
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    // timestamp
    String date;

    // Constructor
    ServerThread(Socket socket) {
      // a unique id
      id = ++uniqueId;
      this.socket = socket;
      // Creating both Data Stream
      System.out.println("Thread trying to create Object Input/Output Streams");
      try {
        sOutput = new ObjectOutputStream(socket.getOutputStream());
        sInput = new ObjectInputStream(socket.getInputStream());
        // read the username
        username = (String) sInput.readObject();
        date = sdf.format(new Date());
        // send data of who is in
        ServerMessage clientJoined = new ServerMessage(ServerMessage.WHOJOINED, username, username + " joined the server");
        broadcast(clientJoined);
      } catch (IOException e) {
        display("Exception creating new Input/output Streams: " + e);
        return;
      } catch (ClassNotFoundException e) {
      }
    }

    public String getUsername() {
      return username;
    }

    // infinite loop to read and forward message
    public void run() {
      // to loop until LOGOUT
      boolean keepGoing = true;
      while (keepGoing) {
        // read a String (which is an ChatMessage)
        try {
          cm = (ClientMessage) sInput.readObject();
        } catch (IOException e) {
          display(username + " Exception reading Streams: " + e);
          break;
        } catch (ClassNotFoundException e2) {
          break;
        }
        // get the message from the ChatMessage object received
        String message = cm.getMessage();

        // different actions based on type message
        switch (cm.getType()) {
          case ClientMessage.MESSAGE:
            ServerMessage msg = new ServerMessage(ServerMessage.MESSAGE, getUsername(), message);
            broadcast(msg);
            break;
          case ClientMessage.WHOISIN:
            ServerMessage userListMsg =
                new ServerMessage(ServerMessage.WHOISIN, "", createUsersString());
            writeMsg(userListMsg);
            break;
          case ClientMessage.DIRECT:
            directMessage(cm);
            break;
        }
      }
      // if out of the loop then disconnected and remove from client list
      remove(id);
      close();
    }

    // close everything
    private void close() {
      try {
        if (sOutput != null) sOutput.close();
      } catch (Exception e) {
      }
      try {
        if (sInput != null) sInput.close();
      } catch (Exception e) {
      }
      try {
        if (socket != null) socket.close();
      } catch (Exception e) {
      }
    }

    // write a message to the Client
    private boolean writeMsg(ServerMessage msg) {
      // if Client is still connected send the message to it
      if (!socket.isConnected()) {
        close();
        return false;
      }
      // write the message to the stream
      try {
        sOutput.writeObject(msg);
      }
      // if an error occurs, do not abort just inform the user
      catch (IOException e) {
        display(notif + "Error sending message to " + username + notif);
        display(e.toString());
      }
      return true;
    }
  }
}
