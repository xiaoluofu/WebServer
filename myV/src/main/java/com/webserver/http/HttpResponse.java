package com.webserver.http;

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

    public HttpResponse(Socket socket){
        this.socket = socket;
    }

    public void flush(){
        sendStatusLine();
        sendHeaders();
        sendContent();
    }


    private void sendStatusLine(){
        try {
            String line = "HTTP/1.1 "+statusCode+" "+statusReason;
            OutputStream out = socket.getOutputStream();
            out.write(line.getBytes("ISO8859-1"));
            out.write(13);
            out.write(10);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendHeaders(){
        try {
            OutputStream out = socket.getOutputStream();
            Set<Map.Entry<String,String>> set = headers.entrySet();
            for (Map.Entry<String,String> e : set) {
                String name = e.getKey();
                String value = e.getValue();

                String line =name + ": "+ value;
                out.write(line.getBytes("ISO8859-1"));
                out.write(13);
                out.write(10);
            }
            out.write(13);
            out.write(10);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendContent(){
        try {
            OutputStream out = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(entity);
            int len;
            byte[] data = new byte[1024*10];
            while ((len = fis.read(data))!=-1){
                out.write(data,0,len);
            }
            System.out.println("消息响应完毕");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        this.entity = entity;

        String[] data = entity.getName().split("\\.");
        String type = HttpContext.getMimeType(data[data.length-1]);
        putHeaders("Content-Type", type);
        putHeaders("Content-length",entity.length()+"");
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

    public void putHeaders(String name,String value){
        this.headers.put(name,value);
    }
}
