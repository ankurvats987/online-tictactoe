import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ArrayList<ClientHandler> clients;
    private BufferedReader clientStreamReader;
    private PrintWriter clientStreamWriter;
    private String clientName;
    private boolean gameStarted = false;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, String name) throws IOException {
        this.clientSocket = clientSocket;
        this.clients = clients;
        clientStreamReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        clientStreamWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        this.clientName = name;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String clientResponse = clientStreamReader.readLine();

                if (clientResponse.equals("play")) {
                    if (!gameStarted) {
                        gameStarted = true;
                        String message = "*READY TO PLAY*";
                        Server.clientsReadyToPlay++;
    
                        message = (Server.clientsReadyToPlay == 2) ? 
                                    message.concat("Y") : message.concat("N");
    
                        messageBroadCast(message, this, true);
                    } else {
                        if (Server.clientsReadyToPlay == 2) {
                            clientStreamWriter.println("[SERVER] Game is already running.");
                        } else {
                            clientStreamWriter.println("[SERVER] Wait for the second player.");
                        }
                    }

                } else if (clientResponse.startsWith("update")) {
                    System.out.println(clientResponse);
                    sendUpdates(clientResponse.substring("update".length(), clientResponse.length()), this);
                } else if (clientResponse.startsWith("*QUITTHEGAME*")) {
                    gameStarted = false;
                    Server.clientsReadyToPlay = 0;
                } else {
                    messageBroadCast(clientResponse, this, false);
                }

            }
        } catch (IOException e) {
            System.err.println("Something went wrong with handler.");
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                clientStreamReader.close();
                clientStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void messageBroadCast(String message, ClientHandler currentClient, boolean readyToPlay) {
        String customPrefix = "[" + currentClient.clientName + "] ";

        for (ClientHandler client : clients) {
            if (!client.equals(currentClient)) {
                String newMessage = (readyToPlay) ? message.concat("1") : message;

                client.clientStreamWriter.println(customPrefix + newMessage);
            } else {
                if (readyToPlay) {
                    client.clientStreamWriter.println(message.concat("2"));
                }
            }
        }
    }

    private void sendUpdates(String update, ClientHandler currentClient) {

        for (ClientHandler client : clients) {
            if (!client.equals(currentClient)) {
                client.clientStreamWriter.println("start".concat(update));
            } else {
                client.clientStreamWriter.println("dontstart");
            }
        }
    }
    
}
