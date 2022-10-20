import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Station {
    private final String name;

    public String getName() {
        return name;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public Object getDate() {
        return date;
    }

    public Object getDepth() {
        return depth;
    }

    public boolean isHasConnection() {
        return hasConnection;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    public void setDepth(Object depth) {
        this.depth = depth;
    }

    public void setHasConnection(boolean hasConnection) {
        this.hasConnection = hasConnection;
    }

      @JsonProperty("number")
    public String lineNumber;
    public Object date;
    public Object depth;
    public boolean hasConnection;

    public Station(String name) {
        this.name = name;
    }


     @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (depth == null && date == null) {    // поменял && на ||
            stringBuilder.append(name);
        } else if (depth == null) {
            stringBuilder.append(name).append(" ").append(date);
        } else if (date == null) {
            stringBuilder.append(name).append(" ").append(depth);
        } else {
            stringBuilder.append(name).append(" ").append(depth).append(" ").append(date);
        }
        return stringBuilder.toString();
    }
}
