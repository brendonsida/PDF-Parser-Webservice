package team.frontend.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtility {

    public static JsonPostRequest parseJsonPostRequest(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, JsonPostRequest.class);
    }
}
