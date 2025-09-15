package cz.cuni.milanenm.receive_json;

import org.json.JSONObject;

/**
 * Utility class responsible for retrieving JSON responses from a given URL.
 */
public class GettingJson {

    /**
     * Fetches and returns the JSON object from the specified URL.
     *
     * @param url The API endpoint URL from which to retrieve the JSON data.
     * @return The JSON object representing the data received from the API.
     */
    public static JSONObject receiveJson (String url) {
        String content = UrlContent.getUrlContent(url);
        System.out.println("API Response: " + content);
        return new JSONObject(content);
    }
}