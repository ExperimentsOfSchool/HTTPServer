import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by Lawrence on 3/14/16.
 */
public class Client {
    public static void main(String[] args) {
        Socket client;
        Scanner scanner = new Scanner(System.in);

        try {
            String fileName;
            String host;
            int port;
            System.out.println("Please input HTTP link:");
            host = scanner.nextLine();
            StringTokenizer address = new StringTokenizer(host, "/");
            host = address.nextToken();
            System.out.println("Please input Port:");
            port = scanner.nextInt();
            client = new Socket(host, port);
            PrintStream writer = new PrintStream(client.getOutputStream());
            fileName = address.hasMoreTokens() ? address.nextToken() : "index.html";
            writer.println("GET /" + fileName + " HTTP/1.1");
            writer.println("Host:" + host);
            writer.println("Connection:keep-alive");
            writer.println();
            writer.flush();
            DataInputStream in = new DataInputStream(client.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String firstLineOfResponse = reader.readLine();
            String secondLineOfResponse = reader.readLine();
            String thirdLineOfResponse = reader.readLine();
            String fourthLineOfResponse = reader.readLine();
            if(firstLineOfResponse.split(" ")[1].equals("200")) {
                File file = new File("./get/" + fileName);

                if(!file.exists()) {
                    while(!file.createNewFile()) {
                        System.out.println("Failed to create file " + fileName);
                    }
                } else {
                    while(!file.setLastModified(System.currentTimeMillis())) {
                        System.out.println("Failed to edit file " + fileName);
                    }
                }
                byte[] buffer = new byte[1024];
                OutputStream fout = new FileOutputStream(file);
                int length = in.read(buffer);
                System.out.println(length);
                while(length != -1) {
//                    for(byte i: buffer) {
//                        System.out.print(i);
//                    }
                    fout.write(buffer, 0, length);
                    length = in.read(buffer);
                }
                in.close();
                fout.close();

            } else {
                System.out.println(fourthLineOfResponse);
            }
            reader.close();
            writer.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
