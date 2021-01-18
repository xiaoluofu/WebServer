package com.webserver.servlet;


import com.webserver.core.WebServer;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RegServlet {

    private static Logger logger = Logger.getLogger(RegServlet.class);

    public void service(HttpRequest request, HttpResponse response){
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        String nikename = request.getParameters("nikename");
        String ageStr = request.getParameters("age");
        logger.info("正在处理注册请求");
        if (username==null || password==null || nikename==null || ageStr==null || !ageStr.matches("[0-9]+")){
            File file = new File("./webapps/myweb/reg_input_error.html");
            response.setEntity(file);
            return;
        }

        int age = Integer.parseInt(ageStr);
        try (
                RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
        ){
            for (int i =0;i<raf.length()/100;i++){
                raf.seek(i*100);
                byte[] data = new byte[32];
                raf.read(data);
                String str = new String(data, StandardCharsets.UTF_8).trim();
                if (str.equals(username)){
                    File file = new File("./webapps/myweb/have_user.html");
                    response.setEntity(file);
                    return;
                }
            }

            raf.seek(raf.length());

            byte[] data = username.getBytes(StandardCharsets.UTF_8);
            data = Arrays.copyOf(data,32);
            raf.write(data);

            data = password.getBytes(StandardCharsets.UTF_8);
            data = Arrays.copyOf(data,32);
            raf.write(data);

            data = nikename.getBytes(StandardCharsets.UTF_8);
            data = Arrays.copyOf(data,32);
            raf.write(data);

            raf.writeInt(age);
            File file = new File("./webapps/myweb/reg_success.html");
            response.setEntity(file);

        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }

        logger.info("注册请求处理完毕");




    }






}
