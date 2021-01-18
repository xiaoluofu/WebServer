package com.webserver.http;

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
public class MyRequest {
    //请求行相关信息

    //请求行中的请求方式
    private String method;
    //抽象路径部分
    private String uri;
    //协议版本
    private String protocol;

    //消息头相关信息
    private Map<String,String> headers = new HashMap<>();

    private Socket socket;
    //消息正文相关信息
    public MyRequest(Socket socket){
        this.socket = socket;
        getRequestLine();
        getRequestHead();
    }
    public void getRequestLine(){
        try {
            String line = readLine();
            System.out.println(line);

            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];
            System.out.println("method:"+method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);

        } catch (IOException e) {   
            e.printStackTrace();
        }
    }
    private void getRequestHead(){
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

}
