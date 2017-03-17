import java.io.*;
import java.net.Socket;
import java.nio.Buffer;

/**
 *
 * Created by Lawrence on 3/14/16.
 */
public class ServerThread extends Thread {
    private Socket client;
    private BufferedReader clientReader;
    private PrintStream clientWriter;
    public ServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println(this.getId());
        try {
            clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientWriter = new PrintStream(client.getOutputStream());
            String firstLineOfRequest = clientReader.readLine();
            String uri = firstLineOfRequest.split(" ")[1];
            uri = uri.equals("/") ? "/index.html" : uri;
            System.out.println(uri);
            File file = new File("./res" + uri);
            FileInputStream fin;
            if(file.exists()) {
                clientWriter.println("HTTP/1.1 200 OK");
                if (uri.endsWith(".html")) {
                    clientWriter.println("Content-Type:text/html");
                } else if (uri.endsWith(".jpg")) {
                    clientWriter.println("Content-Type:image/jpeg");
                } else if (uri.endsWith(".css")) {
                    clientWriter.println("Content-Type:text/css");
                } else if (uri.endsWith(".js")) {
                    clientWriter.println("Content-Type:application/x-javascript");
                } else {
                    clientWriter.println("Content-Type:application/octet-stream");
                }
                fin = new FileInputStream(file);
                clientWriter.println("Content-Length:" + fin.available());
                clientWriter.println();
                byte[] buffer = new byte[1024];
                int length;
                length = 0;
                System.out.println(length);
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                length = fin.read(buffer);
                while(length != -1) {
                    Thread.sleep(100); //Shit bug.
                    out.write(buffer, 0, length);
                    length = fin.read(buffer);
                }
                out.flush();
                fin.close();
            } else {
                clientWriter.println("HTTP/1.1 404 Not Found");
                clientWriter.println("Content-Type:text/plain");
                clientWriter.println("Content-Length:22");
                clientWriter.println();
                clientWriter.println("Can't find such files");
            }
            clientWriter.flush();
            clientReader.close();
            clientWriter.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
