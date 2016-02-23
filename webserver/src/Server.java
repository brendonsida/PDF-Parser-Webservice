
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/extract", new ExtractHandler());
        server.createContext("/highlight", new HighlightHandler());
        server.createContext("/find", new FindHandler());
        server.createContext("/post", new PGetHandler());
        server.createContext("/", new PGetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class FindHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is);
            //
            //Code to connect here
            //
            //Replace below with json doc
            byte[] b = loadFile(fname);
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/pdf");
            responseHeaders.set("Content-Disposition", "attachment; filename=\"" + fname.split("/")[1] + "\"");
            t.sendResponseHeaders(200, b.length);
            OutputStream os = t.getResponseBody();
            os.write(b);
            os.close();
        }
    }

    static class ExtractHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String fname = toPDFFile(is);
            byte[] b = loadFile(fname);
            String coords = getCoordinates(b);

            System.out.println("Coords: " + coords + "\n");
            //
            //Code to connect here
            //
            //

            // loadFile returns the .csv file here or whatever filetype is specified
            byte[] finished = loadFile(toFile(JarExec("../../tabula-java/target/tabula-0.8.0-jar-with-dependencies.jar", fname), "csv"));
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/csv");
            responseHeaders.set("Content-Disposition", "attachment; filename=\"" + fname.split("/")[2] + "\"");
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
            String fname = toPDFFile(is);
            byte[] b = loadFile(fname);
            String coords = getCoordinates(b);
            try {
                System.out.println("inside highlight handler");
                Highlighter.main(new String[] {fname, "coords.txt"});
                System.out.println(fname);
                System.out.println(fname.split("/")[2]);
            } catch (Exception e) {}

            //
            //Code to connect here
            //
            //
            byte[] finished = loadFile(fname);
            Headers responseHeaders = t.getResponseHeaders();
            responseHeaders.set("Content-Type", "application/pdf");
            responseHeaders.set("Content-Disposition", "attachment; filename=\"" + fname.split("/")[1] + "\"");
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
            try {
                File f = new File("../www/pGet.html");
                byte[] b = new byte[(int) f.length()];
                FileInputStream fis = new FileInputStream(f);
                fis.read(b);
                response = new String(b);
            } catch (Exception e) {
                System.out.println(e.getCause());
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static String toPDFFile(InputStream is) {
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
            fname = "../www/" + System.currentTimeMillis() + ".pdf";
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
            fname = "../www/" + System.currentTimeMillis() + "." + type;
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

    static String JarExec(String filepath, String fname) {
        try {
            return ExecTest.main(new String[] {filepath, fname});
        } catch (Exception e) {
            return "Could not run Tabula";
        }
    }

}
