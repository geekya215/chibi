package io.geekya.chibi;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
        String parentDir = generateParent(rootDir, relativeDir);
        Stream<File> dirStream = Arrays.stream(Objects.requireNonNull(Paths.get(currentDir).toFile().listFiles()));
        Stream<File> fileStream = Arrays.stream(Objects.requireNonNull(Paths.get(currentDir).toFile().listFiles()));
        List<String> items = Stream
          .concat(
            dirStream
              .filter(File::isDirectory)
              .sorted(Comparator.comparing(File::getName))
              .map(d -> "<a href=\"" + d.getName() + "/\">" + d.getName() + "/</a>"),
            fileStream
              .filter(File::isFile)
              .sorted(Comparator.comparing(File::getName))
              .map(f -> "<a href=\"" + "/download" + relativeDir + f.getName() + "\">" + f.getName() + "</a>")
          )
          .map(s -> "<div>" + s + "</div>")
          .toList();
        String rest = String.join("\n", items);
        return parentDir + rest;
    }

    public static String generateParent(String rootDir, String relativeDir) {
        if (Objects.equals(relativeDir, "/")) {
            return "";
        } else {
            String currentDirName = Paths.get(rootDir + relativeDir).toFile().getName();
            String parentDir = relativeDir.substring(0, relativeDir.length() - currentDirName.length() - 1);
            return "<div><a href=\"" + parentDir + "\">" + "../</a>\n";
        }
    }

    public static String generateNavigation(String relativeDir) {
        return "<h1>Index of " + relativeDir + "</h1>";
    }

    public static String wrapInHTML(String original) {
        return "<html><link rel=\"shortcut icon\">" + "<body>" + original + "</body></html>";
    }

    public static String generateHttpLog(HttpExchange ctx, int status, long size) {
        String hostAddress = ctx.getRemoteAddress().getAddress().getHostAddress();
        String requestPath = ctx.getRequestURI().getPath();
        String requestMethod = ctx.getRequestMethod();
        String protocol = ctx.getProtocol();
        return String.format("%s - \"%s %s %s\" %d %d", hostAddress, requestMethod, requestPath, protocol, status, size);
    }
}
