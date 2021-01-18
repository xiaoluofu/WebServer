package com.webserver.core;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private ServerSocket serverSocket;

    private static Logger logger = Logger.getLogger(WebServer.class);

    public WebServer(){
        logger.info("正在启动服务端");
        try {
            serverSocket = new ServerSocket(8088);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        logger.info("服务端启动完毕");
    }

    public void start(){
        try {
            while (true) {
                logger.info("正在链接服务器");
                Socket socket = serverSocket.accept();
                logger.info("服务器链接完毕");

                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();
            }

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start();
    }
}
