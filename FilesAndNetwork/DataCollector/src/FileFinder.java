import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileFinder {
    private static List<File> fileListJSON = new ArrayList<>();
    private static List<File> fileListCSV = new ArrayList<>();

    public static void startSearch(String path) {
        File rootFile = new File(path);
        searchFiles(rootFile, fileListJSON, fileListCSV);
    }

    public static void searchFiles(File rootFile, List<File> fileListJSON, List<File> fileListCSV) {
        if (rootFile.isDirectory()) {
            File[] directoryFiles = rootFile.listFiles();
            if (directoryFiles != null) {
                for (File file : directoryFiles) {
                    if (file.isDirectory()) {
                        searchFiles(file, fileListJSON, fileListCSV);
                    } else {
                        if (file.getName().toLowerCase().endsWith("json")) {
                            fileListJSON.add(file);
                            JsonUtils.parseFile(file);
                        } else if (file.getName().toLowerCase().endsWith("csv")) {
                            fileListCSV.add(file);
                            CsvParser.parseFile(file);
                        }
                    }
                }
            }
        }
    }
}