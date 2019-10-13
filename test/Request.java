package cn.com.serverlet02;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {
    //协议信息
    private String requestInfo;
    //请求方式
    private String method;
    //请求的url
    private String url;
    //请求的参数
    private String par;
    private final String CRLF = "\n";
    //封装请求协议 获取method url 以及请求参数
    public Request(Socket client) throws IOException {
        this(client.getInputStream());
    }

    public Request(InputStream is) {
        byte[] datas = new byte[1024*1024];
        int len ;
        try {
            len = is.read(datas);
            this.requestInfo = new String(datas,0,len);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //分解字符串
        parseRequestInfo();
    }
    private void parseRequestInfo() {
        System.out.println("-------分解-------");
        System.out.println(requestInfo);
        System.out.println("---1,获取请求方式：开头到第一个/---");
        this.method = this.requestInfo.substring
                (0,this.requestInfo.indexOf("/")).toLowerCase();//转小写
        this.method = this.method.trim(); //去除前后空格
        System.out.println("---2,获取请求的url：第一个/到HTTP/");
        System.out.println("---可能包含请求参数？前面的为url---");
        //1 获取第一个/的位置
        int startIdx = this.requestInfo.indexOf("/")+1;
        //2 获取HTTP/的位置
        int endIdx = this.requestInfo.indexOf("HTTP/");
        //3 分割字符串
        this.url = this.requestInfo.substring(startIdx,endIdx);
        //4 获取？的位置
        int queryidx = this.url.indexOf("?");
        if (queryidx >= 0) {//表示存在请求参数
            String[] urlArray = this.url.split("\\?");
            this.url = urlArray[0];
            par = urlArray[1];
        }
        System.out.println(this.url);
        System.out.println(this.par);
        System.out.println("---3,获取url后面的请求参数：如果是get,已经获取;如果是post，可能在请求体中---");

        if (method.equals("post")) {
            String qStr = this.requestInfo.substring(this.requestInfo.lastIndexOf(CRLF)).trim();
            if (par == null) {
                par= qStr;
            }else {
                par += "&"+qStr;
            }
        }
        par = par == null ? "": par;
        System.out.println(method+"-->"+url+"-->"+par);
    }
}
