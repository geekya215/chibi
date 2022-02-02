package io.geekya.chibi;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class Main {
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_PATH = ".";
    public static final String DEFAULT_ROOT = "/drive";

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(DEFAULT_PORT), 0);
        httpServer.createContext(DEFAULT_ROOT, ctx -> {
            LOGGER.info("Host: " + ctx.getRemoteAddress().getAddress().getHostAddress() + ", Requested: " + ctx.getRequestURI());
            ctx.getResponseHeaders().set("Content-Type", "text/html");

            try {
                String allFiles = getAllFiles(DEFAULT_PATH);
                String resp =
                        """
                                <html>
                                    <body>
                                """ + allFiles + """
                                     </body>
                                 </html>
                                """;
                ctx.sendResponseHeaders(HTTP_OK, resp.getBytes().length);
                ctx.getResponseBody().write(resp.getBytes());
            } catch (NullPointerException e) {
                String message = e.getMessage();
                LOGGER.warning(message);
                ctx.sendResponseHeaders(HTTP_INTERNAL_ERROR, message.getBytes().length);
                ctx.getResponseBody().write(message.getBytes());
            } finally {
                ctx.close();
            }
        });

        httpServer.createContext("/download", ctx -> {
            String des = ctx.getRequestURI().getPath().substring(10);
            Path p = Paths.get(DEFAULT_PATH).resolve(des);
            long size = p.toFile().length();
            LOGGER.info("Host: " + ctx.getRemoteAddress().getAddress().getHostAddress() + ", Requested: " + ctx.getRequestURI() +
                    ", Downloaded: " + des + ", Size: " + size + " bytes");
            ctx.sendResponseHeaders(HTTP_OK, size);
            ctx.getResponseBody().write(Files.readAllBytes(p));
            ctx.close();
        });

        httpServer.start();
    }

    static String getAllFiles(String path) throws NullPointerException {
        List<String> t = Arrays.stream(Objects.requireNonNull(Paths.get(path).toFile().listFiles()))
                .filter(File::isFile)
                .map(file -> "<a href=\"" + "/download/" + file.getName() + "\">" + file.getName() + "</a>")
                .toList();
        return String.join("<br>", t);
    }
}
