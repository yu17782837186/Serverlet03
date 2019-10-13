package cn.com.serverlet02;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.*;

//封装请求协议：封装请求参数为Map
public class Request2 {
    //协议信息
    private String requestInfo;
    //请求方式
    private String method;
    //请求的url
    private String url;
    //请求的参数
    private String par;
    //存储参数
    private Map<String,List<String>> pMap;
    private final String CRLF = "\n";
    //封装请求协议 获取method url 以及请求参数
    public Request2(Socket client) throws IOException {
        this(client.getInputStream());
    }

    public Request2(InputStream is) {
        pMap = new HashMap<>();
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
        //转成map fav=1&fav=2&name=yuxiaohao&age=18&others=
        convertMap();
    }
    //处理请求参数为Map
    private void convertMap() {
        //1 分割字符串 按 & 分割
        String[] keyValues = this.par.split("&");
        for (String par1: keyValues) {
            //2 再次分割字符串 按 = 分割
            String[] kv = par1.split("=");
            kv = Arrays.copyOf(kv,2);
            //获取key和value
            String key = kv[0];
            String value = kv[1] == null ? null : decode(kv[1],"utf-8");
            //存储到map中
            if (!pMap.containsKey(key)) {//表示第一次
                pMap.put(key,new ArrayList<>());
            }
            pMap.get(key).add(value);
        }
    }
    //处理中文（post）
    private String decode(String value,String enc) {
        try {
            return java.net.URLDecoder.decode(value,enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    //通过（表单）name获取对应的多个值
    public String[] getParameterValues(String key) {
        List<String> list = this.pMap.get(key);
        if (list == null || list.size() < 1) {
            return null;
        }
        return list.toArray(new String[0]);
    }
    //通过name获取对应的一个值
    public String getParameter(String key) {
        String[] values =getParameterValues(key);
        return values == null ? null : values[0];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getPar() {
        return par;
    }

}
