import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerMessageReader implements Runnable {
    private Socket clientSocket;
    private BufferedReader serverStreamReader;
    private Application app;

    public ServerMessageReader(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            serverStreamReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            while (true) {
                String serverResponse = serverStreamReader.readLine();
                
                if (serverResponse.endsWith("Y1") || serverResponse.endsWith("N1") ||
                serverResponse.endsWith("Y2") || serverResponse.endsWith("N2")) {
                    String msg = "";

                    if (serverResponse.endsWith("Y1")) {
                        System.out.println("you are player 1");
                        app = new Application(clientSocket, true, false);
                        app.setInstance(app);
                        msg = "\n[SERVER] Game is running";
                        app.initializeButtons(true, "");
                    } 

                    if (serverResponse.endsWith("Y2")) {
                        System.out.println("you are player 2");

                        app = new Application(clientSocket, false, true);
                        msg = "\n[SERVER] Game is running";
                        app.initializeButtons(false, "");
                        app.setInstance(app);
                    }

                    serverResponse = serverResponse.substring(0, serverResponse.length() - 2).concat(msg);
                }

                if (serverResponse.startsWith("start")) {
                    app.initializeButtons(true, serverResponse.substring("start".length(), serverResponse.length()));
                } else if (serverResponse.startsWith("dontstart")) {
                   app.initializeButtons(false, "00");
                } else {
                    System.out.println(serverResponse);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
