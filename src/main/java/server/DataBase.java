package server;

import com.google.gson.*;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBase {

    private static final String DB_PATH = "./src/server/data/db.json";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock writeLock = lock.writeLock();
    private static final Lock readLock = lock.readLock();

    private DataBase(){}

    public static String setRecord(List<String> keyPath, JsonElement value) {
        JsonElement dataBase = readDataBase();
        writeLock.lock();
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(DB_PATH), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement parent = dataBase;
            boolean found = true;
            for (int i = 0; i < keyPath.size() - 1; i++) {
                String key = keyPath.get(i);
                JsonElement child = parent.getAsJsonObject().get(key);
                if (child == null) {
                    parent.getAsJsonObject().add(key, createChildren(keyPath.subList(i + 1, keyPath.size()), value));
                    found = false;
                    break;
                }
                parent = child;
            }
            if (found) {
                parent.getAsJsonObject().add(keyPath.get(keyPath.size() - 1), value);
            }
            gson.toJson(dataBase, osw);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            writeLock.unlock();
        }
        return Response.jsonOkResponse();
    }

    public static String getRecord(List<String> keyPath) {
        JsonElement parent = readDataBase();
        for (String key : keyPath) {
            JsonElement child = parent.getAsJsonObject().get(key);
            if (child == null) {
                return Response.jsonErrorResponse(Response.NO_SUCH_KEY);
            }
            parent = child;
        }
        return Response.jsonGetResponse(parent);
    }

    public static String deleteRecord(List<String> keyPath) {
        JsonElement dataBase = readDataBase();
        String response = Response.jsonErrorResponse(Response.NO_SUCH_KEY);
        writeLock.lock();
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(DB_PATH), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement parent = dataBase;
            boolean found = true;
            for (int i = 0; i < keyPath.size() - 1; i++) {
                String key = keyPath.get(i);
                JsonElement child = parent.getAsJsonObject().get(key);
                if (child == null) {
                    found = false;
                    break;
                }
                parent = child;
            }
            if (found) {
                parent.getAsJsonObject().remove(keyPath.get(keyPath.size() - 1));
                gson.toJson(dataBase, osw);
                response = Response.jsonOkResponse();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            writeLock.unlock();
        }
        return response;
    }


    private static JsonElement createChildren(List<String> keyPath, JsonElement value) {
        if (keyPath.size() > 1) {
            JsonElement child = new JsonObject();
            child.getAsJsonObject().add(keyPath.get(0), createChildren(keyPath.subList(1, keyPath.size()), value));
            return child;
        } else {
            return value;
        }
    }

    private static JsonElement readDataBase() {
        readLock.lock();
        try (Reader reader = Files.newBufferedReader(Paths.get(DB_PATH),StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonNull()) {
                return element;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            readLock.unlock();
        }
        return new JsonObject();
    }
}
