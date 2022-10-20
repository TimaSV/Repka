import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

public class CsvParser {
    public static void parseFile(File file) {
        try {
            CSVReader reader = new CSVReader(new FileReader(file), ',', '"', 1);
            if (file.getName().toLowerCase().startsWith("depths")) {
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    for (Map.Entry<String, ArrayList<Station>> entry : DataClass.mapLineNumberToStations.entrySet()) {
                        ArrayList<Station> value = entry.getValue();
                        for (int i = 0; i < value.size(); i++) {
                            Station station = value.get(i);
                            if (station.getName().equals(lineInArray[0])) {
                                station.setDepth(lineInArray[1]);
                            }
                        }
                    }
                }
            } else {
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    for (Map.Entry<String, ArrayList<Station>> entry : DataClass.mapLineNumberToStations.entrySet()) {
                        ArrayList<Station> value = entry.getValue();
                        for (int i = 0; i < value.size(); i++) {
                            Station station = value.get(i);
                            if (station.getName().equals(lineInArray[0])) {
                                station.setDate(lineInArray[1]);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
