package io.geekya.chibi;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        final int port = Utils.getPort(args);
        final String dir = Utils.getDir(args);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/", PageHandler.create(dir));
        httpServer.createContext("/download", DownloadHandler.create(dir));
        httpServer.start();
        logger.info("Server started on port " + port + " serving " + dir + " directory" + "\nPress Ctrl+C to stop");
    }
}
