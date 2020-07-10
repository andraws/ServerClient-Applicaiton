package Controllers.controller;

import ServerClient.*;
public interface Controller {
// interface to bind Controller classes
   public default void setTextBubble(ServerMessage msg){}

   public default void updateCount(ServerMessage msg){}

}
