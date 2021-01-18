package com.webserver.http;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Socket socket;
    private String method;
    private String uri;
    private String protocol;

    private Map<String,String> headers = new HashMap<>();

    private static Logger logger = Logger.getLogger(HttpRequest.class);


    public HttpRequest(Socket socket) throws EmptyRequestException {
        this.socket = socket;
        parseRequestLine();
        parseRequestHead();
        parseContext();
    }

    private void parseRequestLine() throws EmptyRequestException {
        logger.info("正在解析请求行");
        try {
            String line = readLine();
            if (!line.isEmpty()){
                String[] data = line.split("\\s");
                method = data[0];
                uri = data[1];
                protocol = data[2];

                logger.info("method:"+method);
                logger.info("uri:"+uri);
                logger.info("protocol:"+protocol);
            }else {
                throw new EmptyRequestException();
            }


        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        logger.info("请求行解析完毕");
    }

    private void parseRequestHead(){
        logger.info("正在解析消息头");
        try {
            while (true) {
                String line = readLine();
                if (line.isEmpty()) {
                    break;
                }
                logger.info("消息头:"+line);
                String[] data = line.split(":\\s");
                headers.put(data[0], data[1]);
            }
            logger.info(headers);

        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }



        logger.info("消息头解析完毕");
    }

    private void parseContext(){
        logger.info("正在解析消息正文");


        logger.info("消息正文解析完毕");
    }

    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        int d;
        char cur='a',per='a';
        StringBuilder builder = new StringBuilder();
        while ((d = in.read())!=-1){
            cur = (char) d;
            if (cur==10&&per==13){
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

    public String getHeaders(String name) {
        return headers.get(name);
    }
}
