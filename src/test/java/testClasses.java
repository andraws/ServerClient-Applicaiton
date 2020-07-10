import ServerClient.Client;
import ServerClient.ClientMessage;
import ServerClient.Output;
import ServerClient.Server;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class testClasses {
    // Testing that the server is running, if it stops running then our running variable would turn false
    @Test
    public void testServerCreation() {
        Output output = new Output();
        // Server goes into infinite loop, need to bypass this
        threadedServer(50,output);
        while (output.getInput().equals(""));
        String update = output.getInput();
        Assert.assertEquals(update, "socketCreated");
    }

    @Test
    public void testServerSamePort() {
        boolean test = false;
        Output output = new Output();
        threadedServer(50,output);
        while (output.getInput().equals(""));
        if(output.getInput().contains("Exception on new ServerSocket"))
            test = true;
        Assert.assertTrue(test);
    }

    @Test
    public void testClientCreation() throws IOException {
        boolean test = false;
        Output out = new Output();
        threadedServer(51,new Output());
        Client client = new Client(51,"Andrew", out);
        while(out.getInput().equals(""));
        System.out.println("out: " + out.getInput());
        if(out.getInput().contains("Connection accepted"))
            test = true;
        Assert.assertTrue(test);
    }

    @Test
    public void writeToServer() throws IOException {
        boolean test = false;
        boolean disconnected = false;
        Output out = new Output();
        Output sOut = new Output();
        threadedServer(52,new Output());
        Client client = new Client(52,"Andrew", out);
        test = client.sendMessage(new ClientMessage(ClientMessage.MESSAGE,"Hello"));
        // if we disconnect we should't be able to send a message
        client.disconnect();
        disconnected = !client.sendMessage(new ClientMessage(ClientMessage.MESSAGE, "Hello"));
        Assert.assertTrue(test && disconnected);
    }


    public void threadedServer(int port, Output out) {
        Thread createServer = new Thread(() -> {
            Server mServer = new Server(port,out);
            mServer.start();
        });
        createServer.start();
    }
}
