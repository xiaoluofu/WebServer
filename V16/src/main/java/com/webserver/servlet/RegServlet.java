package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 处理用户注册业务
 */
public class RegServlet {
    public void service(HttpRequest request, HttpResponse response){
        System.out.println("RegServlet:开始处理注册....");
        //1 获取用户在注册页面上输入的注册信息
        //这里getParameter方法传入的参数值应当与注册页面表单中输入框中name属性指定的值一致!!
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nikename = request.getParameter("nikename");
        String ageStr = request.getParameter("age");
        System.out.println(username+" "+password+" "+nikename+" "+ageStr);

        /*
            添加对用户输入的信息的验证工作
            要求:
            如果用户名，密码，昵称和年龄为null，或者年龄输入的不是一个数字(正则表达式)
            则直接设置response响应一个注册失败的提示页面。
            该页面放在webapps/myweb目录下，名字为reg_input_error.html
            页面中居中显示一行字:这测信息输入有误，请重新注册
            然后加一个超链接返回到注册页面。

            只有验证通过了，才能进行下面的注册操作
         */


        if(username==null || password==null || nikename==null || ageStr==null||!ageStr.matches("[0-9]+")){

            File file = new File("./webapps/myweb/reg_input_error.html");
            response.setEntity(file);
            return;
        }
        //2 将该用户信息写入user.dat文件保存
        int age = Integer.parseInt(ageStr);//将年龄转换为int值
        try (
                RandomAccessFile raf = new RandomAccessFile("user.dat", "rw")
        ) {
            /*
                先读取user.dat文件中现有的所有数据，将没调记录的用户名读取出来并与当前注册用户的用户名
                比对，如果存在，则直接响应页面:hava_user.html
             */

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
            raf.seek(raf.length());//先将指针移动到文件夹末尾，以便追加记录

            byte[] data = username.getBytes(StandardCharsets.UTF_8);
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            data = password.getBytes(StandardCharsets.UTF_8);
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            data = nikename.getBytes(StandardCharsets.UTF_8);
            data = Arrays.copyOf(data, 32);
            raf.write(data);

            raf.writeInt(age);
            System.out.println("注册完毕");

            //3 响应用户注册结果页面(成功或失败的页面)
            File file = new File("./webapps/myweb/reg_success.html");
            response.setEntity(file);

        } catch (IOException e) {
                e.printStackTrace();
        }




        System.out.println("RegServlet:注册处理完毕~");
    }

}
