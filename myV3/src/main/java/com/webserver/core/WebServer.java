package com.webserver.core;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static Logger logger = Logger.getLogger(WebServer.class);
    private ServerSocket serverSocket;

    public WebServer(){

        try {
            logger.info("正在启动服务端");
            serverSocket = new ServerSocket(8088);
            logger.info("服务端启动完毕");
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void start(){
        try {
            while (true) {
                logger.info("正在连接客户端");

                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();

                logger.info("客户端连接完毕");

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
