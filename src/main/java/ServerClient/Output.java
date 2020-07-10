package ServerClient;


public class Output {
  private String input = "";

  public Output() {}

  public void setInput(String in) {
    synchronized (this) {
      this.input = in;
    }
  }

  public String getInput() {
    synchronized (this) {
      return input;
    }
  }
}
