package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Socket socket;
    private String method;
    private String uri;
    private String protocol;
    private String requestURI;
    private String queryString;
    private Map<String,String> parameters = new HashMap<>();


    private Map<String,String> map = new HashMap<>();

    public HttpRequest(Socket socket) throws EmptyRequestException {
        this.socket = socket;
        getRequestHead();
        getRequestLine();
    }

    private void getRequestHead() throws EmptyRequestException {
        try {
            String line = readLine();
            if (line.isEmpty()){
                throw new EmptyRequestException();
            }
            System.out.println("请求行:"+line);

            String[] arr = line.split("\\s");
            method = arr[0];
            uri = arr[1];
            protocol = arr[2];
            parseURI();

            System.out.println("method:"+method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseURI(){

        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        if (!uri.contains("?")){
            requestURI = uri;
        }else{
            System.out.println("HttpRequest:进一步解析uri");
            String[] data = uri.split("\\?");
            requestURI = data[0];
            if (data.length>1){
                queryString = data[1];
                data = queryString.split("&");
                for (String line : data){
                    String[] data2 = line.split("=");
                    if (data2.length ==1){
                        parameters.put(data2[0],null);
                    }else{
                        parameters.put(data2[0],data2[1]);
                    }
                }
            }
        }
        System.out.println("requestURI:"+requestURI);
        System.out.println("queryString:"+queryString);
        System.out.println("parameters:"+parameters);
        System.out.println("HttpRequest:解析完毕");
    }

    private void getRequestLine() {
        try {
            while (true){
                String line =  readLine();
                if (line.isEmpty()){
                    break;
                }
                System.out.println("消息头:"+line);
                String[] arr2 = line.split(":\\s");
                map.put(arr2[0],arr2[1]);
            }
            System.out.println("map:"+map);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private String readLine() throws IOException {
        InputStream is = socket.getInputStream();
        StringBuilder builder = new StringBuilder();
        int d;
        char cur = 'a',per = 'a';
        while ((d = is.read())!=-1){
            cur = (char) d;
            if (cur == 10 && per == 13){
                break;
            }
            per = cur;
            builder.append(cur);
        }
        return builder.toString().trim();
    }


    public Map<String, String> getMap() {
        return map;
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
