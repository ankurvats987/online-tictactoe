import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static final int PORT = 9090;
    static int clientsReadyToPlay = 0;
    private static ServerSocket listener;
    private static ExecutorService pool = Executors.newFixedThreadPool(2);
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            listener = new ServerSocket(PORT, 0, InetAddress.getByName("0.0.0.0"));
            System.out.println("[SERVER] waiting for a client connection...");

            while (true) {
                Socket clientSocket = listener.accept();
                String clientName = RandNameGenerator.getName();

                System.out.printf("[%s] connected.\n", clientName);

                ClientHandler client = new ClientHandler(clientSocket, clients, clientName);
                clients.add(client);
                pool.execute(client);
            }


        } catch (IOException e) {
            System.err.println("[SERVER] connection failed.");
            e.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (Exception e) {
                System.err.println("Something went wrong.");
                e.printStackTrace();
            }
        }
    }
}
