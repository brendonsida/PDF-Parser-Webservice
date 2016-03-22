import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// public enum ResponseType {
//     TABLE_FINDER_POST,
//     TABLE_FINDER_POST_RESPONSE,
//     TABLE_EXTRACTOR_POST,
//     TABLE_EXTRACTOR_POST_RESPONSE,
//     TABLE_HIGHLIGHTER_POST,
//     TABLE_HIGHLIGHTER_POST_RESPONSE
// }

public class JsonUtility {

    public static JsonPostRequest parseJsonPostRequest(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, JsonPostRequest.class);
    }
}
    //     tfPostResponse = null;
    //     filePath = fPath;

    //     InputStream is = JSONUtility.class.getResourceAsStream(filePath);
    //     Gson gson = new GsonBuilder().create();

    //     switch (rType) {
    //         case TABLE_FINDER_POST:
    //             try(Reader reader = new InputStreamReader(is, "UTF-8")) {
    //                 tfPostResponse = gson.fromJson(reader, TableFinderPostResponse.class);
    //             }
    //             break;
    //         case TABLE_FINDER_POST_RESPONSE:
    //             break;
    //         case TABLE_EXTRACTOR_POST:
    //             break;
    //         case TABLE_EXTRACTOR_POST_RESPONSE:

    //             break;
    //         case TABLE_HIGHLIGHTER_POST:
    //             break;
    //         case TABLE_HIGHLIGHTER_POST_RESPONSE:
    //             break;
    //     }
    // }

    // public ArrayList<TableToParse> getTableCoordsForTabula() {
    //     return tfPostResponse.getCoordinates();
    // }
// }
