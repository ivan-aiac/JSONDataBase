package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Response {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String NO_SUCH_KEY = "No such key";

    private Response(){}

    public static String jsonOkResponse() {
        return response(OK, null, null);
    }

    public static String jsonGetResponse(JsonElement value) {
        return response(OK, value, null);
    }

    public static String jsonErrorResponse(String reason) {
        return response(ERROR, null, reason);
    }

    private static String response (String response, JsonElement value, String reason) {
        JsonObject res = new JsonObject();
        res.addProperty("response", response);
        res.add("value", value);
        res.addProperty("reason", reason);
        Gson gson = new Gson();
        return gson.toJson(res);
    }
}
