package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpContext;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.RegServlet;

import java.awt.dnd.DropTarget;
import java.io.*;

import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ClientHandler implements Runnable{
    private Socket socket;



    public ClientHandler(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        try {


            HttpRequest request = new HttpRequest(socket);
            HttpResponse response = new HttpResponse(socket);

            String path = request.getRequestURI();
            File file = new File("./webapps"+path);

            if ("/myweb/regUser".equals(path)){
                RegServlet servlet = new RegServlet();
                servlet.service(request,response);


            }else{
                if (file.exists() && file.isFile()){
                    response.setEntity(file);
                }else {
                    file = new File("./webapps/root/404.html");
                    response.setStatusCode(404);
                    response.setStatusReason("NotFound");
                    response.setEntity(file);
                }
            }


            response.putHeaders("Server","WebServer");

            response.flush();


        }catch (EmptyRequestException e){

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
