package com.webserver.core;

import com.webserver.servlet.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类保存所有服务端公用信息
 */
public class ServerContext {
    private static Map<String, HttpServlet> servletMapping = new HashMap<>();

    private static Logger logger = Logger.getLogger(ServerContext.class);
    static{
        initServletMapping();
    }

    private static void initServletMapping(){
//        servletMapping.put("/myweb/regUser",new RegServlet());
//        servletMapping.put("/myweb/showAllUser",new ShowAllUserServlet());
//        servletMapping.put("/myweb/loginUser",new LoginServlet());
//        servletMapping.put("/myweb/updateUser",new UpdataServlet());


        /*
            通过解析config/servlets.xml文件初始化servletMapping
            实现：
            将servlets.xml文件中根标签下所有<servlet>标签获取到，并将其中的属性:
            path的值作为key。class的值利用反射实例化对应的Servlet作为value并存到
            servletMapping中完成初始化
            注:反射实例化后返回值时Object类型，需要造型为HttpServlet才能存入到map
         */


        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read("./config/servlets.xml");

            Element root = doc.getRootElement();
            List<Element> list = root.elements("servlet");

            for (Element e : list){
                String path = e.attributeValue("path");
                String classname = e.attributeValue("classname");

                Class<?> cls = Class.forName(classname);
                Object o = cls.newInstance();
                servletMapping.put(path,(HttpServlet)o);
            }
            logger.info("servletMapping:"+servletMapping);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据请求路径获取对应的Servlet
     * @param path
     * @return
     */

    public static HttpServlet getServlet(String path){
        return servletMapping.get(path);
    }

}
