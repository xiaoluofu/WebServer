package com.webserver.http;


import org.apache.log4j.Logger;

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

    private Map<String, String> map = new HashMap<>();

    private static Logger logger = Logger.getLogger(HttpRequest.class);


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
            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];

            paraseURI();

            logger.info("method:" + method);
            logger.info("uri:"+uri);
            logger.info("protocol:"+protocol);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

    }
    //myweb/regUser?username=xiaoluofu123&password=gaokaixuan&nikename=kuang&age=20
    private void paraseURI(){

        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!uri.contains("?")){
            requestURI = uri;
        }else {
            String[] data = uri.split("\\?");
            requestURI = data[0];
            if (data.length == 1) {
                return;
            }
            queryString = data[1];
            data = queryString.split("&");
            for (String str : data) {
                String[] data2 = str.split("=");
                if (data2.length == 1) {
                    parameters.put(data2[0], null);
                } else {
                    parameters.put(data2[0], data2[1]);
                }
            }
        }

    }




    private void getRequestLine() {
        try {

            while (true){
                String line = readLine();
                if (line.isEmpty()){
                    break;
                }
                logger.info("消息头:"+line);
                String[] data = line.split(":\\s");
                map.put(data[0],data[1]);
            }
            logger.info("map:"+map);


        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }


    private String readLine() throws IOException {

            InputStream in = socket.getInputStream();
            char cur = 'a', per = 'a';
            StringBuilder builder = new StringBuilder();
            int d;
            while ((d = in.read()) != -1) {
                cur = (char) d;
                if (cur == 10 && per == 13) {
                    break;
                }
                per = cur;
                builder.append(cur);
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

    public Map<String, String> getMap() {
        return map;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameters(String name) {
        return parameters.get(name);
    }
}
