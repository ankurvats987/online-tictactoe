import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static Socket socket;
    private static BufferedReader keyboardInput;
    private static PrintWriter serverStreamWriter;

    public static void main(String[] args) {
        try {
            socket = new Socket("172.18.182.88", 9090);
            System.out.println("[SERVER] connection established.");

            new Thread(new ServerMessageReader(socket)).start();

            keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            serverStreamWriter = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String input = keyboardInput.readLine();

                serverStreamWriter.println(input);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                keyboardInput.close();
                serverStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }   
}
