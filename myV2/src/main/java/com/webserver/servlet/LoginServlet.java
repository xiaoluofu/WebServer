package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class LoginServlet {

    private static Logger logger = Logger.getLogger(LoginServlet.class);

    public void service(HttpRequest request, HttpResponse response){
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        logger.info("正在处理登录业务");
        try (
                RandomAccessFile raf = new RandomAccessFile("user.dat","r")
        ){
            for (int i =0;i<raf.length()/100;i++){
                raf.seek(i*100);
                byte[] data = new byte[32];
                raf.read(data);
                String str = new String(data, StandardCharsets.UTF_8).trim();
                if (str.equals(username)){
                    raf.read(data);
                    str = new String(data, StandardCharsets.UTF_8).trim();
                    if (str.equals(password)){
                        File file = new File("./webapps/myweb/login_success.html");
                        response.setEntity(file);
                        return;
                    }
                    break;
                }
            }
            File file = new File("./webapps/myweb/login_fail.html");
            response.setEntity(file);
            logger.info("登录业务处理完毕");

        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }


    }



}
