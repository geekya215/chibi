# chibi
chibi is a static file server written in Java, which uses only the Java built-in http server to 
handle requests. The entire application is packaged in a mere 9 kb.

**Note that chibi does not have any security protection, so do not use it in the WAN!**

## Usage
```
java -jar chibi.jar [options] [arguments]
Options:
 -p Server port (default: 8080)
 -d Server root directory (default: current directory)
```
