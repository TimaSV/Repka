import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class DataClass {
    public static HashMap<String, ArrayList<Station>> mapLineNumberToStations = new HashMap<>();
    public static List<Line> metroLines = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> mapJson = new HashMap<>();
}
