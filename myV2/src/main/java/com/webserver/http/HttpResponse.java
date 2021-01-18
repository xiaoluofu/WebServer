package com.webserver.http;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private Socket socket;

    private int statusCode = 200;
    private String statusReason = "OK";

    private File entity;
    private byte[] contentData;

    private Map<String,String> headers = new HashMap<>();
    private static Logger logger = Logger.getLogger(HttpResponse.class);


    public HttpResponse(Socket socket){
        this.socket = socket;
    }


    public void flush(){
        sendStatusLine();
        sendHeaders();
        sendContext();
    }

    private void sendStatusLine(){
        logger.info("HttpResponse:正在发送请求行");
        try {

            OutputStream out = socket.getOutputStream();
            String line = "HTTP/1.1 "+statusCode+" "+statusReason;
            out.write(line.getBytes(StandardCharsets.UTF_8));
            out.write(13);
            out.write(10);

        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        logger.info("HttpResponse:请求行发送完毕");
    }

    private void sendHeaders(){
        logger.info("HttpResponse:正在发送请求头");
        try {
            OutputStream out = socket.getOutputStream();

            Set<Map.Entry<String,String>> set = headers.entrySet();
            for (Map.Entry<String,String> e : set){
                String name = e.getKey();
                String value = e.getValue();
                String line = name+" "+value;
                out.write(line.getBytes(StandardCharsets.UTF_8));
                out.write(13);
                out.write(10);
            }

            out.write(13);
            out.write(10);


        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        logger.info("HttpResponse:请求头发送完毕");
    }

    private void sendContext(){
        logger.info("HttpResponse:正在发送请求正文");
        try {
            if (contentData!=null){
                try {
                    OutputStream out = socket.getOutputStream();
                    out.write(contentData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (entity!=null) {
                OutputStream out = socket.getOutputStream();
                FileInputStream fis = new FileInputStream(entity);
                int d;
                byte[] data = new byte[1024 * 10];
                while ((d = fis.read(data)) != -1) {
                    out.write(data, 0, d);
                }
            }
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
        logger.info("HttpResponse:请求正文发送完毕");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        this.entity = entity;

        String[] data = entity.getName().split("\\.");
        String type = HttpContext.getMimeMapping(data[data.length - 1]);
        putHeaders("Content-Type:",type);
        putHeaders("Content-Length:",entity.length()+"");

    }

    public void putHeaders(String name,String value){
        headers.put(name,value);
    }

    public void setContentData(byte[] contentData) {
        this.contentData = contentData;
        putHeaders("Content-Length",contentData.length+"");
    }
}
