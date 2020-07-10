package ServerClient;

import java.io.Serializable;

public class ClientMessage implements Serializable {
  // The different types of message sent by the Client
  // WHOISIN to receive the list of the users connected
  // MESSAGE an ordinary text message
  // DIRECT to send message to the server dedicated to someone, followed by who to send to
  public static final int WHOISIN = 0, MESSAGE = 1, DIRECT = 2;
  private int type;
  private String message;
  private String user;

  // used for WHOISIN, and MESSAGE
  public ClientMessage(int type, String message) {
    this.type = type;
    this.message = message;
  }
  // used for DIRECT
  public ClientMessage(int type, String sender, String receiver, String message) {
    this.type = type;
    this.user = sender + "@" + receiver;
    this.message = message;
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
}
