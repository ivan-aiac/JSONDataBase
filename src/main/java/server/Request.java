package server;

import com.beust.jcommander.internal.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Request {

    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private String type;
    private List<String> keyPath;
    private JsonElement jsonValue;

    public Request(){}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(List<String> keyPath) {
        this.keyPath = keyPath;
    }

    public JsonElement getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(JsonElement jsonValue) {
        this.jsonValue = jsonValue;
    }

    public static Request fromJsonObject(JsonObject json) {
        Request request = new Request();
        request.setType(json.getAsJsonPrimitive(TYPE).getAsString());
        JsonElement key = json.get(KEY);
        if (key != null) {
            if (key.isJsonPrimitive()) {
                request.setKeyPath(Lists.newArrayList(key.getAsString()));
            } else {
                List<String> keyList = new ArrayList<>();
                key.getAsJsonArray().forEach(e -> keyList.add(e.getAsString()));
                request.setKeyPath(keyList);
            }
            JsonElement value = json.get(VALUE);
            if (value != null) {
                request.setJsonValue(value);
            }
        }
        return request;
    }
}
