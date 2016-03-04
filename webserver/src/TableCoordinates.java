import java.util.*;
import java.io.*; 


public class TableCoordinates {
    private String page;
    private Float y1;
    private Float x1;
    private Float y2;
    private Float x2;

    public String getPage() {
        return page;
    }

    public String getCoordinates() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(y1) + ",");
        sb.append(String.valueOf(x1) + ",");
        sb.append(String.valueOf(y2) + ",");
        sb.append(String.valueOf(x2));
        return sb.toString();
    }

    public String asArguments() {
        StringBuilder sb = new StringBuilder();
        sb.append("-a ");
        sb.append(String.valueOf(y1) + ",");
        sb.append(String.valueOf(x1) + ",");
        sb.append(String.valueOf(y2) + ",");
        sb.append(String.valueOf(x2) + " ");
        sb.append("-p ");
        sb.append(page);
        return sb.toString();
    }

    @Override
    public String toString() {
        String pageNum = "Page Number: " + page + "\n";
        String Y1 = "Y1: " + String.valueOf(y1) + "\n";
        String X1 = "X1: " + String.valueOf(x1) + "\n";
        String Y2 = "Y2: " + String.valueOf(y2) + "\n";
        String X2 = "X2: " + String.valueOf(x2);
        return "" + pageNum + Y1 + X1 + Y2 + X2 + "";
    }
}
