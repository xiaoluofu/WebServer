package com.webserver.http;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpContext {
    private static Map<String,String> mimeMapping = new HashMap<>();
    static {
        initMapping();
    }

    private static void initMapping(){
        try {
            SAXReader reader =new SAXReader();
            Document doc = reader.read("./config/web.xml");
            Element root = doc.getRootElement();
            List<Element> list = root.elements("mime-mapping");
            for (Element e : list){
                mimeMapping.put(e.elementText("extension"),e.elementText("mime-type"));
            }
            System.out.println("mimeMapping:"+mimeMapping.size());

        }catch (Exception e){

        }
    }

    public static String getMimeType(String mime){
        return mimeMapping.get(mime);
    }
}
