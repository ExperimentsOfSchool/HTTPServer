
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TestClient {

    //发送请求头
    public static void requesthead(PrintStream writer, String filename){
        writer.println("GET /" + filename + " HTTP/1.1");
        writer.println("Host:localhost");
        writer.println("connection:keep-alive");
        writer.println();
        writer.flush();
    }

    //发送请求体
    public static void requestbody()
            throws IOException{

    }

    //接收响应状态
    public static void receive(InputStream in, BufferedReader reader, String savelocation, String filename)
            throws IOException{

        //响应成功（状态码 200）
        //跳过响应中的前四行，开始读取响应数据
        String firstLineOfResponse = reader.readLine();
        if(firstLineOfResponse.equals("HTTP/1.1 200 OK")){
            String secondLineOfResponse = reader.readLine();
            String threeLineOfResponse = reader.readLine();
            String fourLineOfResponse = reader.readLine();

            //读取响应数据，保存文件
            //success
            byte[] b = new byte[1024];
            int process = 0;

            File file = new File(savelocation+"/"+filename);

            if(!file.exists()){
                file.createNewFile();
            }
            OutputStream out = new FileOutputStream(savelocation+"/"+filename);

            System.out.println("开始接收！");
            int len = in.read(b);
            while(len!=-1)
            {
                out.write(b, 0, len);
                process++;
                len = in.read(b);
            }

            System.out.println("成功！");

            in.close();
            out.close();
        }else if(firstLineOfResponse.equals("HTTP/1.1 404 Not Found")){
            String secondLineOfResponse = reader.readLine();
            String threeLineOfResponse = reader.readLine();
            String fourLineOfResponse = reader.readLine();
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            System. out.println(result);
        }
    }


    public static void main(String[] args)
            throws UnknownHostException, IOException{

        Socket s = new Socket("127.0.0.1", 2333);
        PrintStream writer = new PrintStream(s.getOutputStream());
        InputStream in = s.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String filename = null;
        String savelocation = null;

        System.out.println("请输入保存路径的名称");
        savelocation = new Scanner(System.in).nextLine();
        System.out.println("请输入下载文件的名称");
        filename = new Scanner(System.in).nextLine();

        requesthead(writer, filename);
        receive(in, reader, savelocation, filename);

        System.exit(0);
    }
}