package team.frontend.app;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Scanner;

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
        server.createContext("/highlight", new HighlightHandler());
        server.createContext("/find", new FindHandler());
        server.createContext("/post", new PGetHandler());

        server.createContext("/", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class FindHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is,1);
            byte[] b = loadFile(fname);
            //
            //Code to connect here
            //
            //Replace below with json doc
            byte[] finished = JarExec("../../tabula-java/target/tabula-0.8.0-jar-with-dependencies.jar",fname, new String[]{"-G","-i"}).getBytes();
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            responseHeaders.set("Content-Disposition", "render; filename=\"" + "Finder_" + fname + ".json" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
        }
    }

    static class AutoExtractHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is,1);
            byte[] finished = loadFile(toFile(JarExec("../../tabula-java/target/tabula-0.8.0-jar-with-dependencies.jar",fname, new String[]{"-g","-fJSON"}), "csv"));
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/csv");
            //responseHeaders.set("Content-Disposition", "attachment; filename=\"" + System.currentTimeMillis() + ".csv" + "\"");
            responseHeaders.set("Content-Disposition", "render; filename=\"" + "Extractor_" + fname + ".json" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
        }
    }
    static class ExtractHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is,2);
            byte[] b = loadFile(fname);

            // JSON Start
            String out = "";
            String json = getJSON(b);
            String filename = getFilename(b,2);            
            System.out.printf(json);
            JsonPostRequest req = null;
            req = JsonUtility.parseJsonPostRequest(json);
            int numTablesToParse = req.getNumTablesToParse()-1;
            for (int i=0; i < numTablesToParse; i++) {
                TableCoordinates table = req.getTableCoordinate(i);
                String coords = table.getCoordinates();
                String pageNum = table.getPage();
                System.out.printf("coords: %s, pageNum: %s\n", coords, pageNum);
                // TODO: Need to redirect System.out to return to the "out" String var
                CommandLineApp.main(new String[] {fname, "-a", coords, "-p", pageNum});
            }
            // JSON End
            // loadFile returns the .csv file here or whatever filetype is specified
            byte[] finished = out.getBytes();
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/json");
            //responseHeaders.set("Content-Disposition", "attachment; filename=\"" + System.currentTimeMillis() + ".csv" + "\"");
            responseHeaders.set("Content-Disposition", "inline; filename=\"" + "Extractor_" + filename + ".json" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
        }
    }

    static class HighlightHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is,2);
            byte[] b = loadFile(fname);
            System.out.println("This is the file name "+ fname);
            // JSON Start
            String out = "";
            String json = getJSON(b);
            String filename = getFilename(b,2);            
            System.out.printf(json);
            JsonPostRequest req = null;
            req = JsonUtility.parseJsonPostRequest(json);
            int numTablesToParse = req.getNumTablesToParse()-1;
            
            // JSON End
            try {
                for (int i=0; i < numTablesToParse; i++) {
                TableCoordinates table = req.getTableCoordinate(i);
                String tabulaArgs = table.highlighterArguments();
                System.out.println("we are here sir");
                Highlighter.main(new String[] {fname, tabulaArgs});           
            }
            } catch (Exception e) {}
            byte[] finished = loadFile(fname);
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/pdf");
            responseHeaders.set("Content-Disposition", "render; filename=\"" + System.currentTimeMillis() + ".pdf" + "\"");
            t.sendResponseHeaders(200, finished.length);
            OutputStream os = t.getResponseBody();
            os.write(finished);
            os.close();
        }
    }



    static class PGetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the find response \n" + t.getRequestMethod() + "\n" + t.getRequestHeaders().toString();
            byte[] b = loadFile("./src/www/test.html");
            t.sendResponseHeaders(200, b.length);
            OutputStream os = t.getResponseBody();
            os.write(b);
            os.close();
        }
    }

    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the 404 response \n" + t.getRequestMethod() + "\n" + t.getRequestHeaders().toString();
            System.out.println(t.getRequestURI());
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
            fname = "./src/www/" + getFilename(b,filecount);
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
    
    static String getFilename(byte[]b,int filecount) {
        String s = new String(b);
        Scanner scan = new Scanner(s);
        scan.useDelimiter("filename=\"");
        scan.next();
        if(filecount>1){
            scan.next();
        }
        String filename = scan.nextLine();
        return filename.split("\"")[1];
    }

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
            if (!temp.contains("-WebKit")&& !temp.contains("----")) {
                json  = json + temp + "\n";
            } else {
                end = !end;
            }
        }
        return json;
    }

    static String JarExec(String filepath, String fname, String[] args) {
        try {
            return ExecTest.main(new String[] {filepath,args[0],args[1], fname});
        } catch (Exception e) {
            return "Could not run Tabula";
        }
    }

}
