package io.geekya.chibi;

import com.sun.net.httpserver.HttpExchange;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Utils {
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_DIR = ".";

    public static int getPort(String[] args) {
        final List<String> argList = Arrays.asList(args);
        return argList.contains("-p") ? Integer.parseInt(getArg(args, "-p")) : DEFAULT_PORT;
    }

    public static String getDir(String[] args) {
        final List<String> argList = Arrays.asList(args);
        return argList.contains("-d") ? getArg(args, "-d") : DEFAULT_DIR;
    }

    public static String getArg(String[] args, String arg) {
        final int index = Arrays.asList(args).indexOf(arg);
        return args[index + 1];
    }

    public static String generateDirList(String rootDir, String relativeDir) throws NullPointerException {
        String currentDir = rootDir + relativeDir;
        return String.join("\n", Arrays.stream(Objects.requireNonNull(Paths.get(currentDir).toFile().listFiles()))
                .map(f -> f.isDirectory() ? "<a href=\"" + f.getName() + "/\">" + f.getName() + "/</a>" : "<a href=\"" + "/download" + relativeDir + f.getName() + "\">" + f.getName() + "</a>")
                .map(s -> "<div>" + s + "</div>").toList());
    }

    public static String generateNavigation(String path) {
        return "<h1>Index of " + path + "</h1>";
    }

    public static String wrapInHTML(String original) {
        return "<html><body>" + original + "</body></html>";
    }

    public static String generateHttpLog(HttpExchange ctx, int status, long size) {
        String hostAddress = ctx.getRemoteAddress().getAddress().getHostAddress();
        String requestPath = ctx.getRequestURI().getPath();
        String requestMethod = ctx.getRequestMethod();
        String protocol = ctx.getProtocol();
        return String.format("%s - \"%s %s %s\" %d %d", hostAddress, requestMethod, requestPath, protocol, status, size);
    }
}
