import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.xml.crypto.Data;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {
    public static HashMap<String, Station> mapForParsing = new HashMap<>();

    public static void parseFile(File file) {
        try {
            Object obj = new JSONParser().parse(new FileReader(file));
            JSONArray jo = (JSONArray) obj;
            if (file.getName().toLowerCase().startsWith("depths")) {   
                jo.forEach(depthObject -> {
                    JSONObject depthJsonObject = (JSONObject) depthObject;
                    for (Map.Entry<String, ArrayList<Station>> entry : DataClass.mapLineNumberToStations.entrySet()) {
                        String key = entry.getKey();
                        ArrayList<Station> value = entry.getValue();
                        for (int i = 0; i < value.size(); i++) {
                            Station station = value.get(i);
                            if (station.getName().equals(depthJsonObject.get("name"))) {
                                station.setDepth(depthJsonObject.get("depth"));
                            }
                        }
                    }
                });

            } else {
                jo.forEach(dateObject -> {
                    JSONObject dateJsonObject = (JSONObject) dateObject;
                    for (Map.Entry<String, ArrayList<Station>> entry : DataClass.mapLineNumberToStations.entrySet()) {
                        String key = entry.getKey();
                        ArrayList<Station> value = entry.getValue();
                        for (int i = 0; i < value.size(); i++) {
                            Station station = value.get(i);
                            if (station.getName().equals(dateJsonObject.get("name"))) {
                                station.setDate(dateJsonObject.get("date"));
                            }
                        }
                    }
                });

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void CreateJsonFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("stations.json").toFile(), DataClass.mapLineNumberToStations);
            JSONObject map = new JSONObject();
            map.put("stations",  DataClass.mapJson);
            map.put("lines", DataClass.metroLines);
            mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get("map.json").toFile(), map);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
