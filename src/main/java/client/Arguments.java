package client;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arguments {

    private static final String PATH = "./src/client/data/";

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-t", description = "Type of Request")
    private String type;

    @Parameter(names = "-k", variableArity = true, description = "JSON key")
    private List<String> key;

    @Parameter(names = "-v", variableArity = true, description = "JSON value")
    private List<String> value;

    @Parameter(names = "-in", description = "Input file name")
    private String file;

    public Arguments() {}

    public String toJson() {
        if (file != null) {
            String json = "";
            try (FileInputStream fis = new FileInputStream(PATH + file)) {
                json = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return json;
        } else {
            Gson gson = new Gson();
            Map<String, String> request = new HashMap<>();
            request.put("type", type);
            if (key != null) {
                request.put("key", String.join(" ", key));
            }
            if (value != null) {
                request.put("value", String.join(" ", value));
            }
            return gson.toJson(request);
        }
    }
}
