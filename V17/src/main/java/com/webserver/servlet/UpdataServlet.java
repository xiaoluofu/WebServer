package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UpdataServlet extends HttpServlet{


    public void service( HttpRequest request,HttpResponse response){
        String username = request.getParameter("username");
        String oldpassword = request.getParameter("oldpassword");
        String newpassword = request.getParameter("newpassword");

        try(
                RandomAccessFile raf = new RandomAccessFile("user.dat","rw");
        ) {
            boolean b = false;
            for (int i =0;i<raf.length()/100;i++) {
                raf.seek(i * 100);
                byte[] data = new byte[32];
                raf.read(data);
                String str = new String(data, "UTF-8").trim();
                if (str.equals(username)){
                    data = new byte[32];
                    raf.read(data);
                    str = new String(data,"UTF-8").trim();
                    if (str.equals(oldpassword)){
                        b = true;
                        raf.seek(i*100+32);
                        data = newpassword.getBytes(StandardCharsets.UTF_8);
                        data = Arrays.copyOf(data,32);
                        raf.write(data);
                        break;
                    }
                    break;
                }
            }
            if (b){
                File file = new File("./webapps/myweb/update_success.html");
                response.setEntity(file);
            }else{
                File file = new File("./webapps/myweb/update_fail.html");
                response.setEntity(file);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
