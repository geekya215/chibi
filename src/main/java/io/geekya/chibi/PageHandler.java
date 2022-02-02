package io.geekya.chibi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Logger;

import static io.geekya.chibi.Utils.*;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class PageHandler implements HttpHandler {
    private final Logger logger = Logger.getLogger(PageHandler.class.getName());
    private final String dir;

    private PageHandler(String dir) {
        this.dir = dir;
    }

    public static HttpHandler create(String dir) {
        return new PageHandler(dir);
    }

    @Override
    public void handle(HttpExchange ctx) throws IOException {
        try {
            String path = ctx.getRequestURI().getPath();
            String page = wrapInHTML(generateNavigation(path) + generateDirList(dir, path));

            ctx.getResponseHeaders().set("Content-Type", "text/html");
            ctx.sendResponseHeaders(HTTP_OK, page.getBytes().length);
            ctx.getResponseBody().write(page.getBytes());

            logger.info(generateHttpLog(ctx, HTTP_OK, page.getBytes().length));
        } catch (NullPointerException e) {
            String ERROR_MESSAGE = "Oops! Something went wrong.";

            ctx.sendResponseHeaders(HTTP_INTERNAL_ERROR, ERROR_MESSAGE.getBytes().length);
            ctx.getResponseBody().write(ERROR_MESSAGE.getBytes());

            logger.warning(generateHttpLog(ctx, HTTP_INTERNAL_ERROR, ERROR_MESSAGE.getBytes().length));
            logger.warning(e.getMessage());
        } finally {
            ctx.close();
        }
    }
}
