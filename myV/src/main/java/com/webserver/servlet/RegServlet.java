package com.webserver.servlet;


import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RegServlet {


    public void service(HttpRequest request, HttpResponse response){
        try (
                RandomAccessFile raf = new RandomAccessFile("./user.dat","rw")
        ){

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String nikename = request.getParameter("nikename");
            String ageStr = request.getParameter("age");

            String ageMatch = "[0-9]+";
            if (username==null || password==null || nikename==null || ageStr==null || !ageStr.matches(ageMatch)){
                File file = new File("./webapps/myweb/reg_input_error.html");
                response.setEntity(file);
                return;
            }



            for (int i =0;i<raf.length()/100;i++){
                raf.seek(i*100);
                byte[] data = new byte[32];
                raf.read(data);
                String str = new String(data,"UTF-8").trim();
                if (str.equals(username)){
                    File file = new File("./webapps/myweb/have_user.html");
                    response.setEntity(file);
                    return;
                }
            }

            int age = Integer.parseInt(ageStr);

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
            e.printStackTrace();
        }
    }

}
