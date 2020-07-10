package ServerClient;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ServerMessage implements Serializable {
  // The different types of messages sent by the Server
  // WHOLEFT to notify who left, followed by user
  // WHOJOINED to notify who joined, followed by user
  // DIRECT to send a direct message to a user, followed by which user it was sent by
  // MESSAGE an ordinary text message
  // WHOISIN returns all users that is in the server

  public static final int WHOLEFT = 0, WHOJOINED = 1, DIRECT = 2, MESSAGE = 3, WHOISIN = 4;
  private int type;
  private String timeStamp = "";
  private String user;
  private String message;
  private List<String> onlineUsers = new LinkedList<>();

  public ServerMessage(int type, String user, String message) {
    this.type = type;
    this.user = user;
    this.message = message;
    if (type == WHOISIN || type == WHOLEFT || type == WHOJOINED) {
      createList();
    }
  }

  private void createList() {
    String[] parts = message.split(" ");
    for (String user : parts) {
      onlineUsers.add(user);
    }
  }

  public List<String> getOnlineUsers() {
    return onlineUsers;
  }

  public int getType() {
    return type;
  }

  public String getUser() {
    return user;
  }

  public String getMessage() {
    return message;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void updateTimeStamp(String time) {
    this.timeStamp = time;
  }

  public String systemFormat() {
    return "[" + "Type: " + type + " " + timeStamp + " " + user + "]: " + message + "\n";
  }
}
