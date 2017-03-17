import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lawrence on 3/16/16.
 */
public class HTTPServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(2333);
            Socket socket;
            while (true) {
                socket = server.accept();
                new ServerThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
