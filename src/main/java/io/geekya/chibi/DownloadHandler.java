package io.geekya.chibi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

public class DownloadHandler implements HttpHandler {
    private final Logger logger = Logger.getLogger(PageHandler.class.getName());
    private final String dir;

    private DownloadHandler(String dir) {
        this.dir = dir;
    }

    public static HttpHandler create(String dir) {
        return new DownloadHandler(dir);
    }

    @Override
    public void handle(HttpExchange ctx) throws IOException {
        String source = ctx.getRequestURI().getPath().substring(9);
        File f = new File(dir + source);

        if (f.exists()) {
            ctx.sendResponseHeaders(HTTP_OK, f.length());
            ctx.getResponseBody().write(Files.readAllBytes(f.toPath()));

            logger.info(Utils.generateHttpLog(ctx, HTTP_OK, f.length()));
        } else {
            String NOT_FOUND = "File not found";
            ctx.sendResponseHeaders(HTTP_NOT_FOUND, NOT_FOUND.getBytes().length);
            ctx.getResponseBody().write(NOT_FOUND.getBytes());

            logger.warning(Utils.generateHttpLog(ctx, HTTP_NOT_FOUND, NOT_FOUND.getBytes().length));
        }
        ctx.close();
    }
}
