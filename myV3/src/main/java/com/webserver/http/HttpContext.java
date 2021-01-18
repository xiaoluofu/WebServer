package com.webserver.http;


import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpContext {
    private static Map<String,String> mimeMapping = new HashMap<>();

    private static Logger logger = Logger.getLogger(HttpContext.class);

    static{
        initMimeMapping();
    }

    private static void initMimeMapping(){

        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read("./config/web.xml");
            Element root = doc.getRootElement();
            List<Element> list = root.elements("mimeMapping");
            for (Element e : list){
                mimeMapping.put(e.elementText("extension"),e.elementText("mime-type"));
            }
            logger.info("mimeMapping:"+mimeMapping.size());

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    public static String getMimeMapping(String name) {
        return mimeMapping.get(name);
    }
}
