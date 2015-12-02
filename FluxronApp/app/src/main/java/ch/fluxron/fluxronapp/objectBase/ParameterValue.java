package ch.fluxron.fluxronapp.objectBase;

/**
 * A value object for a parameter/field its value.
 */
public class ParameterValue {
    private String name;
    private String value;

    /**
     * Instantiates a new ParameterValue.
     *
     * @param name
     * @param value
     */
    public ParameterValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the parameter.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the parameter.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the value of this parameter.
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this parameter.
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
