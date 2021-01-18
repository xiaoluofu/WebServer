package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

import com.webserver.servlet.ShowAllUsersServlet;
import org.apache.log4j.Logger;


import java.io.File;

import java.io.IOException;
import java.net.Socket;


public class ClientHandler implements Runnable {
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


            String path = request.getRequestURI();

            if ("/myweb/regUser".equals(path)){
                RegServlet servlet = new RegServlet();
                servlet.service(request,response);
            }else if("/myweb/showAllUser".equals(path)){
                ShowAllUsersServlet servlet = new ShowAllUsersServlet();
                servlet.service(request,response);
            }else if ("/myweb/loginUser".equals(path)){
                LoginServlet servlet = new LoginServlet();
                servlet.service(request,response);
            } else {
                File file = new File("./webapps" + path);
                if (file.isFile()&&file.exists()) {
                    response.setEntity(file);
                }else {
                    file = new File("./webapps/root/404.html");
                    response.setStatusCode(404);
                    response.setStatusReason("Not Found");
                    response.setEntity(file);
                }
            }
            response.flush();



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
