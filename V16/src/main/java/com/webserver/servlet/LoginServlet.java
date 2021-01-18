package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LoginServlet {


    public void service(HttpRequest request, HttpResponse response){
        System.out.println("处理登录信息........");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try (
                RandomAccessFile raf = new RandomAccessFile("user.dat","rw")
        ){
            boolean b = false;
            for (int i =0;i<raf.length()/100;i++){
                raf.seek(i*100);
                byte[] data = new byte[32];
                raf.read(data);
                String str = new String(data,"UTF-8").trim();
                if (str.equals(username)){
                    data = new byte[32];
                    raf.read(data);
                    str = new String(data,"UTF-8").trim();
                    if (str.equals(password)){
                        b = true;
                        break;
                    }
                    break;
                }
            }
            if (b){
                File file = new File("./webapps/myweb/login_success.html");
                response.setEntity(file);
            }else{
                File file = new File("./webapps/myweb/login_fail.html");
                response.setEntity(file);
            }


        }catch (IOException e){
            e.printStackTrace();
        }



        System.out.println("登录信息处理完毕");

    }
}
