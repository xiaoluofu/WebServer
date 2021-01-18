package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.vo.User;
import org.apache.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ShowAllUsersServlet {
    private static Logger logger = Logger.getLogger(ShowAllUsersServlet.class);

    public void service(HttpRequest request, HttpResponse response){
        logger.info("正在处理展示所有用户的业务");

        List<User> list = new ArrayList<>();
        try (
                RandomAccessFile raf = new RandomAccessFile("user.dat","r")
        ){
            for (int i =0;i<raf.length()/100;i++){
                byte[] data = new byte[32];
                raf.read(data);
                String username = new String(data, StandardCharsets.UTF_8).trim();

                raf.read(data);
                String password = new String(data,StandardCharsets.UTF_8).trim();

                raf.read(data);
                String nikename = new String(data,StandardCharsets.UTF_8).trim();

                int age = raf.readInt();
                User user = new User(username,password,nikename,age);
                list.add(user);
            }
            for (User user : list){
                logger.info(user);
            }

        }catch (IOException e){
            logger.error(e.getMessage(),e);
        }

        Context context = new Context();
        context.setVariable("users",list);
        FileTemplateResolver ftr = new FileTemplateResolver();
        ftr.setTemplateMode("html");
        ftr.setCharacterEncoding("UTF-8");
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(ftr);
        String html = engine.process("./webapps/myweb/userlist.html", context);
        logger.info(html);

        byte[] data = html.getBytes(StandardCharsets.UTF_8);
        response.setContentData(data);
        response.putHeaders("Content-Type","text/html");


        logger.info("处理展示所有用户业务完毕");
    }


}
