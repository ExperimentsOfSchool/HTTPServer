/**
 * Created by Lawrence on 3/21/16.
 */
import java.io.*;
import java.net.*;

public class TestServer {

    //根据客户端请求文件的后缀名，发送响应类型
    @SuppressWarnings("resource")
    public static FileInputStream styleresponset(BufferedReader reader, PrintStream writer, FileInputStream in)
            throws IOException{

        String firstLineOfRequest;
        firstLineOfRequest = reader.readLine();

        String url = firstLineOfRequest.split(" ")[1];
        writer.println("HTTP/1.1 200 OK");//返回应答信息，并结束应答
        if(url.endsWith(".html")){
            writer.println("Content-Type:text/html");
        }else if(url.endsWith(".jpg")){
            writer.println("Content-Type:image/jpeg");
        }else{
            writer.println("Content-Type:application/octet-stream");
        }

        try{
            in = new FileInputStream("./res/" + url);//文件位置
            System.out.println("./res/" + url);
            //发送响应头
            writer.println("Content-Length:" + in.available());//返回内容字节数
            writer.println();//根据HTTP协议， 空行将结束头信息

            writer.flush();

            return in;
        }catch(FileNotFoundException e){
            return null;
        }
    }

    //发送响应数据；
    public static void dataresponse(DataOutputStream os, FileInputStream in)
            throws IOException{
        byte[] b = new byte[1024];
        int len = 0;

        System.out.println("开始传输！");
        len = in.read(b);
        while(len != -1){
            os.write(b, 0, len);
            len = in.read(b);
        }

        System.out.println("传输完成！");
        os.flush();
    }

    //请求资源不存在的情况下，发送纯文本的响应信息。
    public static void noresource(PrintStream writer){
        writer.println("HTTP/1.1 404 NOT Found");
        writer.println("Content-Type:text/plain");
        writer.println("Content-Length:7");
        writer.println();

        //发送响应体
        writer.print("访问内容不存在");
        writer.flush();
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException{
        ServerSocket ss = null;

        try{
            ss = new ServerSocket(2333);
        }catch(IOException e){
            e.printStackTrace();
        }
        //开始监听，分配子线程
        while(true){
            try{
                System.out.println("开始监听");
                Socket s = ss.accept();

                FileInputStream in = null;
                DataOutputStream os = new DataOutputStream(s.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintStream writer = new PrintStream(s.getOutputStream());

                in = styleresponset(reader, writer, in);

                if(in != null){
                    dataresponse(os, in);
                }else{
                    noresource(writer);
                }

                s.close();

            }catch(IOException e1){
                e1.printStackTrace();
            }
        }
    }
}