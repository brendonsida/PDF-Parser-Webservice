package team.front.end;
import java.util.*;

public class JsonPostRequest {

    private String fileName;
    private String fileUrl;
    private ArrayList<TableCoordinates> coordinates;

    public int getNumTablesToParse() {
        return coordinates.size();
    }

    public String getTabulaArgsForTable(int index) {
        return coordinates.get(index).asArguments();
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public String toString() {
        String fileInfo;
        StringBuilder sb;
        ListIterator<TableCoordinates> li;
        TableCoordinates c;

        li = coordinates.listIterator();
        sb = new StringBuilder();

        fileInfo = "File Name: " + fileName + ", File URL: " + fileUrl;
        sb.append(fileInfo + "\n");
        sb.append("Coordinates Info:" + "\n");
        c = li.next();
        while (c != null) {
            sb.append(c.toString());
            sb.append("\n");
            c = li.next();
        }
        
        return sb.toString();
    }
}
