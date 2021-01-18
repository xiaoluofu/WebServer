package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable{
    private Socket socket;

    private static Logger logger = Logger.getLogger(ClientHandler.class);

    public ClientHandler(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            HttpRequest request = new HttpRequest(socket);
            HttpResponse response = new HttpResponse(socket);

            String path = request.getUri();
            File file = new File("./webapps"+path);

            logger.info("开始发送响应...");

            if (file.exists()&&file.isFile()){
                response.setEntity(file);
            }else{
                File fileNotFound = new File("./webapps/root/404.html");
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setEntity(fileNotFound);
            }
            response.flush();

            logger.info("发送响应完毕");



        }catch (EmptyRequestException e){

        } catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
