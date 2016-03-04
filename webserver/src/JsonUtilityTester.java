// package com.teamfrontend.json;
// import org.json.*;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtilityTester {

    /* This is my first java program.
     * This will print 'Hello World' as the output
     */

    public static void main(String []args) throws IOException {
        String jsonString = "{";
                jsonString += "\"fileName\": \"name-of-file-parsed.pdf\",";
                jsonString += "\"fileUrl\": \"maybe-path-to-file?\",";
                jsonString += "\"coordinates\": [";
                jsonString += "     {";
                jsonString += "         \"page\": \"1\",";
                jsonString += "         \"y1\": 254.3625,";
                jsonString += "         \"x1\": 48.5775,";
                jsonString += "         \"y2\": 382.1175,";
                jsonString += "         \"x2\": 531.2925";
                jsonString += "     }";
                jsonString += " ]";
                jsonString += "}";
        JsonPostRequest r = JsonUtility.parseJsonPostRequest(jsonString);
        System.out.println(r.getTabulaArgsForTable(0));
    }
}
