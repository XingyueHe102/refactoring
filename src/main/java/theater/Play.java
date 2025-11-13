package theater;

/**
 * Represents a play with a name and type.
 * @null both fields are non-null
 */
public class Play {

    private String name;
    private String type;

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getPlayName() {
        return name;
    }

    public String getPlayType() {
        return type;
    }
}
