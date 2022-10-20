public class Line {

    private final String name;
    private final String number;


    public Line(String name, String number) {
        this.name = name;
        this.number = number;
    }


    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return number + "." + name;
    }
}