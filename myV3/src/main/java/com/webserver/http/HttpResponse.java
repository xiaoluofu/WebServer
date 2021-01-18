package com.webserver.http;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private Socket socket;

    private int statusCode = 200;
    private String statusReason = "OK";

    private File entity;

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
        try {
            String line = "HTTP/1.1 "+statusCode+" "+statusReason;
            OutputStream out = socket.getOutputStream();
            out.write(line.getBytes("ISO8859-1"));
            out.write(13);
            out.write(10);
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

    private void sendHeaders(){
        try {
            OutputStream out = socket.getOutputStream();

            Set<Map.Entry<String,String>> set = headers.entrySet();
            for (Map.Entry<String,String> e : set){
                String name = e.getKey();
                String value = e.getValue();
                String line = name+" "+value;
                out.write(line.getBytes("ISO8859-1"));
                out.write(13);
                out.write(10);

                line = "Content-Length: "+entity.length();
                out.write(line.getBytes("ISO8859-1"));
                out.write(13);
                out.write(10);
            }
            out.write(13);
            out.write(10);


        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

    private void sendContext(){
        try {
            FileInputStream fis = new FileInputStream(entity);
            OutputStream out = socket.getOutputStream();
            byte[] data = new byte[1024*10];
            int len;
            while ((len = fis.read(data))!=-1){
                out.write(data,0,len);
            }
        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public void setEntity(File entity) {
        this.entity = entity;
    }

    public void putHeaders(String name,String value){
        headers.put(name,value);
    }
}
