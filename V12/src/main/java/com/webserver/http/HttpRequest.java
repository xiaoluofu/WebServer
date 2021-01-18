package com.webserver.http;

import com.webserver.core.ClientHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类的每一个实例用于表示HTTP的一个请求
 * 每个请求有三部分组成:请求行，消息头，消息正文
 */
public class HttpRequest {
    //请求行相关信息

    //请求行中的请求方式
    private String method;
    //抽象路径部分
    private String uri;
    //协议版本
    private String protocol;

    //保存uri中的请求部分(?左侧内容)
    private String requestURI;
    //保存uri中的参数部分(?右侧内容)
    private String queryString;
    //保存每一组参数，key:参数名 value:参数值
    private Map<String,String> parameters = new HashMap<>();

    //消息头相关信息
    private Map<String, String> headers = new HashMap<>();
    //消息正文相关信息

    //和连接相关的属性
    private Socket socket;

    /**
     * 构造方法，在实例化HttpRequest的同时完成了解析请求的工作
     * @param socket
     */
    public HttpRequest(Socket socket) throws EmptyRequestException {
        this.socket = socket;
        /*
            1:解析请求行
            2:解析消息头
            3:解析消息正文
         */
        parseRequestLine();
        parseHeaders();
        parseContent();
    }
    private void parseRequestLine() throws EmptyRequestException {
        System.out.println("HttpRequest:解析请求行...");
        try {
            String line = readLine();
            //如果读取请求行内容返回是空串，说明本次为空请求
            if (line.isEmpty()){
                throw new EmptyRequestException();
            }
            System.out.println(line);

            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];

            //进一步解析uri
            parseUri();

            System.out.println("method:"+method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("HttpRequest:请求行解析完毕");
    }

    /**
     * 进一步对uri进行拆分解析，因为uri可能包含参数。
     */
    private void parseUri(){
        System.out.println("HttpRequest:进一步解析uri");
        /*
            uri可能存在两种情况:含有参数，不含有参数
            含有参数(uri中包含"?"):
            首先赞找"?"将uri拆分为两部分，第一部分赋值给requestURI这个属性。第二部分赋给
            queryString这个属性
            然后将queryString(uri中的参数部分)进一步拆分，来得到每一组参数。
            首先将queryString按照"&"拆分出每一组参数，然后每组参数再按照"="拆分为参数名与参数值
            之后将参数名作为key，参数值作为value保存到parameters这个Map中保存即可。

            不含有参数(uri中不包含"?")
            则直接将uri的值赋值给requestURI即可
         */
        if (!uri.contains("?")){
            requestURI = uri;
        }else{
            String[] data = uri.split("\\?");
            requestURI = data[0];

            if (data.length > 1) {//确定是否有参数部分
                queryString = data[1];
                //拆分出每一组参数
                data = queryString.split("&");
                //遍历
                for (String line : data) {
                    String[] data2 = line.split("=");
                    if (data2.length == 1) {
                        parameters.put(data2[0], null);
                    } else {
                        parameters.put(data2[0], data2[1]);
                    }
                }
            }

        }
        System.out.println("requestURI:"+requestURI);
        System.out.println("queryString:"+queryString);
        System.out.println("parameters:"+parameters);



        System.out.println("HttpRequest:进一步解析uri完毕！");
    }

    private void parseHeaders(){
        System.out.println("HttpRequest:解析消息头...");
        try {
            while (true) {
                String line = readLine();
                if (line.isEmpty()) {
                    break;
                }
                System.out.println("消息头:" + line);
                String[] data2 = line.split(":\\s");
                headers.put(data2[0], data2[1]);
            }
            System.out.println("headers:"+headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("HttpRequest:消息头解析完毕!");
    }
    private void parseContent(){
        System.out.println("HttpRequest:解析消息正文...");

        System.out.println("HttpRequest:消息正文解析完毕!");
    }



    private String readLine() throws IOException {
        /*
            同一个socket对象，无论调用多少次getInputStream方法
            获取到的输入流都是同一个
         */
        InputStream in = socket.getInputStream();
        int d;
        //记录读取到的一行字符串
        StringBuilder builder = new StringBuilder();

        //cur表示本次读取的字符，pre表示上次读取的字符
        char cur = 'a',pre = 'a';
        while ((d = in.read())!=-1){
            cur = (char)d;
            //如果上次读取的是回车符并且本次读取的是换行符就停止
            if (pre == 13 && cur ==10){
                break;
            }
            builder.append(cur);
            pre = cur;
        }
        return builder.toString().trim();
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

}