import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser {
   // private static final String htmlFile = "data1/code.html";
    // private static final String DATA_FILE = "src/main/resources/map.json";



    public static void metroHTMLParser() {
        try {
            String htmlFile = parseFile("E:\\Repozitoriy\\dpo_java_basics\\FilesAndNetwork\\DataCollector\\data1\\code.html");

            Document metro = Jsoup.parse(htmlFile);
            Elements lines = metro.select("span.js-metro-line");
            lines.forEach(element -> {
                DataClass.metroLines.add(new Line(element.text(), element.attr("data-line")));
            });
            Elements stations = metro.select("div.js-metro-stations[data-line]");
            stations.forEach(element -> {
                String lineNumber = element.attr("data-line");
                Elements stationNameElements = element.select("p.single-station > span.name");
                ArrayList<Station> lineStations = new ArrayList<>();
                stationNameElements.forEach(st -> {
                    Station station = new Station(st.text() );   /// добавил "\n" +
                    station.setLineNumber(lineNumber);
                    lineStations.add(station);
                });
                ArrayList<String> stationsForSecondMap = new ArrayList<>();
                stationNameElements.forEach(st -> {
                    stationsForSecondMap.add(st.text());
                });
                DataClass.mapLineNumberToStations.put(lineNumber, lineStations);
                DataClass.mapJson.put(lineNumber, stationsForSecondMap);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static String parseFile(String path)
    {
        StringBuilder builder = new StringBuilder();
        try
        {
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line->builder.append(line + "\n"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return  builder.toString();
    }






}


