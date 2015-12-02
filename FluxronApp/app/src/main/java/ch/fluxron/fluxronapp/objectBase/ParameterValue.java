package ch.fluxron.fluxronapp.objectBase;

/**
 * A device parameter
 */
public class ParameterValue {
    private String name;
    private String value;

    /**
     * Creates a new parameter
     *
     * @param name  Name of the parameter
     * @param value Value of the parameter
     */
    public ParameterValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of the parameter
     *
     * @return Name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the parameter
     *
     * @param name Name of the parameter
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the parameter
     *
     * @return Value of the parameter
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the parameter
     *
     * @param value Value of the parameter
     */
    public void setValue(String value) {
        this.value = value;
    }
}
