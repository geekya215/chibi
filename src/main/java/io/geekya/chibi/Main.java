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

import static java.net.HttpURLConnection.*;

public class Main {
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_PATH = ".";
    public static final String DEFAULT_ROOT = "/drive";

    public static void main(String[] args) throws IOException {
        final int port = getPort(args);
        final String path = getPath(args);
        final String root = getRoot(args);
        final HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        httpServer.createContext(root, ctx -> {
            LOGGER.info("Host: " + ctx.getRemoteAddress().getAddress().getHostAddress() + ", Requested: " + ctx.getRequestURI());
            ctx.getResponseHeaders().set("Content-Type", "text/html");

            try {
                String allFiles = getAllFiles(path);
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
            Path p = Paths.get(path).resolve(des);
            if (p.toFile().exists()) {
                long size = p.toFile().length();
                LOGGER.info("Host: " + ctx.getRemoteAddress().getAddress().getHostAddress() + ", Requested: " + ctx.getRequestURI() +
                        ", Downloaded: " + des + ", Size: " + size + " bytes");
                ctx.sendResponseHeaders(HTTP_OK, size);
                ctx.getResponseBody().write(Files.readAllBytes(p));
                ctx.close();
            } else {
                ctx.sendResponseHeaders(HTTP_NOT_FOUND, "File not found".getBytes().length);
                ctx.getResponseBody().write("File not found".getBytes());
                ctx.close();
            }
        });
        httpServer.setExecutor(null);
        httpServer.start();
        LOGGER.info("Server started on port: " + port + ", root: " + root + ", path: " + path);
    }

    private static int getPort(String[] args) {
        final List<String> argList = Arrays.asList(args);
        return argList.contains("-p") ? Integer.parseInt(getArg(args, "-p")) : DEFAULT_PORT;
    }

    private static String getPath(String[] args) {
        final List<String> argList = Arrays.asList(args);
        return argList.contains("-d") ? getArg(args, "-d") : DEFAULT_PATH;
    }

    private static String getRoot(String[] args) {
        final List<String> argList = Arrays.asList(args);
        return argList.contains("-r") ? getArg(args, "-r") : DEFAULT_ROOT;
    }

    private static String getArg(String[] args, String arg) {
        final int index = Arrays.asList(args).indexOf(arg);
        return args[index + 1];
    }

    private static String getAllFiles(String path) throws NullPointerException {
        List<String> t = Arrays.stream(Objects.requireNonNull(Paths.get(path).toFile().listFiles()))
                .filter(File::isFile)
                .map(file -> "<a href=\"" + "/download/" + file.getName() + "\">" + file.getName() + "</a>")
                .toList();
        return String.join("<br>", t);
    }
}
