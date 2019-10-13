package cn.com.serverlet02;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

public class Server04 {
    //封装请求信息
    private ServerSocket serverSocket ;
    public static void main(String[] args) {
        Server04 server = new Server04();
        server.start();
    }
    //启动服务
    public void start() {
        try {
            serverSocket =  new ServerSocket(9999);
            receive();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败....");
        }
    }
    //接受连接处理
    public void receive() {
        try {
            Socket client = serverSocket.accept();
            System.out.println("一个客户端建立了连接....");
            //获取请求协议
            Request request = new Request(client);

            Response response = new Response(client);
            //关注了内容
            response.print("<html>");
            response.print("<head>");
            response.print("<title>");
            response.print("服务器响应成功");
            response.print("</title>");
            response.print("</head>");
            response.print("<body>");
            response.print("shsxt server终于回来了。。。。");
            response.print("</body>");
            response.print("</html>");
            //关注了状态码
            response.pushToBrowser(200);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端错误");
        }
    }
    //停止服务
    public void stop() {

    }
}
