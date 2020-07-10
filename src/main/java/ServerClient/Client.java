package ServerClient;
// A simple Client Server Protocol .. Client for Echo Server

import Controllers.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// The Client that can be run as a console
public class Client {

  ScreenController sController = ScreenController.getInstance();
  // notification
  private String notif = " *** ";

  private Output out;

  // for I/O
  private ObjectInputStream sInput; // to read from the socket
  private ObjectOutputStream sOutput; // to write on the socket
  private Socket socket; // socket object

  private String server = "localhost";
  private String username; // server and username
  private int port; // port
  private boolean connected;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /*
   *  Constructor to set below things
   *  server: the server address
   *  port: the port number
   *  username: the username
   *  out: output object
   */
  public Client(int port, String username, Output out) throws IOException {
    this.out = out;
    this.port = port;
    setUsername(username);
    // try to connect to the server and return if not connected
    if (!this.start())
    {
      connected = false;
      return;
    }else{
      connected = true;
    }
    sController.setClient(this);
  }

  /*
   * To start the chat
   */
  public boolean start() {
    // try to connect to the server
    try {
      socket = new Socket(server, port);
    }
    // exception handler if it failed
    catch (Exception ec) {
      display("Error connecting to server:" + ec);
      return false;
    }

    String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
    display(msg);

    /* Creating both Data Stream */
    try {
      sInput = new ObjectInputStream(socket.getInputStream());
      sOutput = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException eIO) {
      display("Exception creating new Input/output Streams: " + eIO);
      return false;
    }

    // creates the Thread to listen from the server
    new ListenerThread().start();
    // Send our username to the server as string, all other messages will be ClientMessages
    try {
      sOutput.writeObject(username);
    } catch (IOException eIO) {
      display("Exception doing login : " + eIO);
      disconnect();
      return false;
    }
    // success we inform the caller that it worked
    return true;
  }

  /*
   * Display messages
   */
  public void display(String msg) {
    out.setInput(msg);
    //System.out.println(msg);
  }

  /*
   * To send a message to the server
   */
  public boolean sendMessage(ClientMessage msg) {
    try {
      sOutput.writeObject(msg);
      return true;
    } catch (IOException e) {
      display("Exception writing to server: " + e);
      return false;
    }
  }

  /*
   * When something goes wrong
   * Close the Input/Output streams and disconnect
   */
  public void disconnect() {
    try {
      if (sInput != null) sInput.close();
    } catch (Exception e) {
    }
    try {
      if (sOutput != null) sOutput.close();
    } catch (Exception e) {
    }
    try {
      if (socket != null) socket.close();
    } catch (Exception e) {
    }
  }

  public boolean getConnected(){
    return connected;
  }

  /*
   * a Thread that waits for the message from the server
   */
  class ListenerThread extends Thread {

    public void run() {
      while (true) {
        try {
          // read the message form the input datastream as ServerMessage
          ServerMessage msg = (ServerMessage) sInput.readObject();
          // if it is Tester thread, all we are trying to do is check if username is taken
          if (getUsername().equals("Tester")) {
            sController.getTestMsg(msg);
          } else {
            display(msg.getMessage());
            sController.displayMessage(msg);
          }
        } catch (IOException e) {
          display(notif + "Server has closed the connection: " + e + notif);
          break;
        } catch (ClassNotFoundException e2) {
        }
      }
    }
  }
}
