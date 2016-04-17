package team.frontend.app;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.security.*;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import technology.tabula.CommandLineApp;

public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/extract", new AutoExtractHandler());
        server.createContext("/extract2", new ExtractHandler());
        server.createContext("/tablehighlight", new HighlightHandler());
        server.createContext("/find", new FindHandler());

        server.createContext("/", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    //Static hander for Find http context, see API for usage
    static class FindHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //Get input from POST
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is,1);
            byte[] b = loadFile(fname);
            PrintStream sysout = System.out;
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            
            //Capture output from Finder
            System.setOut(new PrintStream(bs));
            Finder.main(new String[] {fname});
            String out = bs.toString();
            byte[] finished = out.getBytes();
            System.setOut(sysout);
            
            //Respond to post request
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            System.out.println("fname ="+fname);
            //Set Headers to show in browser
            responseHeaders.set("Content-Disposition", "render; filename=\"" + "Finder_" +  fname.split("/")[3].replace(".pdf","") + ".json" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
            
            //Remove temp file
            deleteFile(fname);
        }
    }
    //Static hander for Auto Extract(Find+Extract) http context, see Webservice API for usage
    static class AutoExtractHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //Get input from POST
            String out = "";
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is, 1);
            
            //Capture output from Tabula using guess flag
            PrintStream sysout = System.out;
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bs));
            try{
              forbidSystemExitCall();
              CommandLineApp.main(new String[] {fname, "-g"});
              }catch(SecurityException e){
              }finally{
              enableSystemExitCall();
              System.setOut(sysout);
            }
            out = bs.toString();
            byte[] finished = out.getBytes();
            
            //Respond to post request
            Headers responseHeaders = t.getResponseHeaders();
            //Set Headers to show in browser
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("Content-Disposition", "inline; filename=\"" + fname.split("/")[3].replace(".pdf","") + ".csv" + "\"");
            //responseHeaders.set("Content-Disposition", "render; filename=\"" + "Extractor_" + fname.split("/")[3].replace(".pdf","") + ".json" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
            
            //Remove temp file
            deleteFile(fname);
        }
    }
    
    //Static hander for Extract http context, see API for usage
    static class ExtractHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is, 2);
            byte[] b = loadFile(fname);

            // JSON Start
            String out = "";
            String json = getJSON(b);
            String filename = getFilename(b, 2);
            JsonPostRequest req = null;
            req = JsonUtility.parseJsonPostRequest(json);
            int numTablesToParse = req.getNumTablesToParse() - 1;
            // JSON End
            
            //Parse Tables
            PrintStream sysout = System.out;
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bs));
            forbidSystemExitCall();
            try {
                for (int i = 0; i < numTablesToParse; i++) {
                    TableCoordinates table = req.getTableCoordinate(i);
                    String coords = table.getCoordinates();
                    String pageNum = table.getPage();
                    System.err.printf("coords: %s, pageNum: %s\n", coords, pageNum);
                    try{
                    forbidSystemExitCall();
                    CommandLineApp.main(new String[] {fname, "-a", coords, "-p", pageNum});
                    }catch(SecurityException e){
                    }finally{
                      enableSystemExitCall();
                    }
                }
            }finally {
                System.setOut(sysout);   
            }
            out = bs.toString();
            
            //Send Response
            byte[] finished = out.getBytes();
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("Content-Disposition", "inline; filename=\"" + fname.split("/")[3].replace(".pdf","") + ".csv" + "\"");
            //responseHeaders.set("Content-Disposition", "inline; filename=\"" + "Extractor_" +  fname.split("/")[3].replace(".pdf","") + ".json" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
            deleteFile(fname);
        }
    }
    
    //Static hander for tablehighlight http context, see API for usage
    static class HighlightHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //Preprocess request
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is, 2);
            byte[] b = loadFile(fname);
            System.out.println("This is the file name " + fname);
            
            // JSON  Processing Start
            String out = "";
            String json = getJSON(b);
            String filename = getFilename(b, 2);
            JsonPostRequest req = null;
            req = JsonUtility.parseJsonPostRequest(json);
            int numTablesToParse = req.getNumTablesToParse() - 1;
            // JSON End
            
            //Highlight tables
            try {
                for (int i = 0; i < numTablesToParse; i++) {
                    TableCoordinates table = req.getTableCoordinate(i);
                    String tabulaArgs = table.highlighterArguments();
                    Highlighter.main(new String[] {fname, tabulaArgs});
                }
            } catch (Exception e) {}
            
            //Http Response configure and send
            byte[] finished = loadFile(fname);
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/pdf");
            responseHeaders.set("Content-Disposition", "render; filename=\"" + fname.split("/")[3]  + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
            deleteFile(fname);
        }
    }
  
    //Get response for UI files
    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //Handle request for files on Server
            String response = "This is the 404 response \n" + t.getRequestMethod() + "\n" + t.getRequestHeaders().toString();
            System.out.println("User requested: " + t.getRequestURI());
            Headers responseHeaders = t.getResponseHeaders();
            if (t.getRequestURI().toString().contains("png")) {
                responseHeaders.set("Content-Type", "image/png");
            }
            byte[] b = null;
            try {
                File f;
                if (t.getRequestURI().toString().length() < 2) {
                    b = loadFile("./src/www/index.html");
                } else {
                    b = loadFile("./src/www/" + t.getRequestURI());
                }
            } catch (Exception e) {
                System.out.println(e.getCause());
            }
            t.sendResponseHeaders(200, b.length);
            OutputStream os = t.getResponseBody();
            os.write(b);
            os.close();
        }
    }
    
    //Temporarily store pdf, and retrive filename from request
    static String toPDFFile(InputStream is, int filecount) {
        String fname = "";
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] b = buffer.toByteArray();
            fname = "./src/www/" + getFilename(b, filecount);
            File f = new File(fname);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(b);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return "";
        }
        return fname;
    }

    //General purpose file save
    static String toFile(String contents, String type) {
        String fname = "";
        try {

            byte[] b = contents.getBytes();
            fname = "./src/www/" + System.currentTimeMillis() + "." + type;
            File f = new File(fname);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(b);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return "";
        }
        return fname;
    }

    //General purpose load file 
    static byte[] loadFile(String fname) {
        byte[]b;
        try {
            File f = new File(fname);
            FileInputStream fis = new FileInputStream(f);
            b = new byte[(int) f.length()];
            fis.read(b);
            fis.close();
        } catch (Exception e) {
            return null;
        }
        return b;
    }
  
    //Retrieve coordinates from request
    static String getCoordinates(byte[]b) {
        String s = new String(b);
        Scanner scan = new Scanner(s);
        scan.useDelimiter("Coordinates");
        scan.next();
        scan.nextLine();
        scan.nextLine();
        String coords = scan.nextLine();
        return coords;
    }

    //Retrieve filename from request
    static String getFilename(byte[]b, int filecount) {
        String s = new String(b);
        Scanner scan = new Scanner(s);
        scan.useDelimiter("filename=\"");
        scan.next();
        if (filecount > 1) {
            scan.next();
        }
        String filename = scan.nextLine();
        return filename.split("\"")[1];
    }
    
    //Retrieve JSON from request
    static String getJSON(byte[]b) {
        String json = "";
        String s = new String(b);
        Scanner scan = new Scanner(s);
        scan.useDelimiter("CoordDoc");
        scan.next();
        scan.nextLine();
        scan.nextLine();
        boolean end = false;
        while (!end) {
            String temp = scan.nextLine();
            if (!temp.contains("-WebKit") && !temp.contains("----")) {
                json  = json + temp + "\n";
            } else {
                end = !end;
            }
        }
        return json;
    }
    
    //Delete file
    private static void deleteFile(String fname){
      File f = new File(fname);
      f.delete();
    }
    
    // Helper functions to use Tabula without the Server dying
    // Allows for selective ignorance of System.exit() calls
    private static class ExitTrappedException extends SecurityException { }
      
    private static void forbidSystemExitCall() {
      final SecurityManager securityManager = new SecurityManager() {
        public void checkPermission(Permission permission) {
          if( "exitVM.*".equals( permission.getName() ) ) {
            throw new ExitTrappedException() ;
          }
        }
        public void checkExit(int status){
          throw new SecurityException();
        }
      } ;
      System.setSecurityManager( securityManager ) ;
    }
  
    private static void enableSystemExitCall() {
      System.setSecurityManager( null ) ;
    }
    //End of Helper functions 
    
}
