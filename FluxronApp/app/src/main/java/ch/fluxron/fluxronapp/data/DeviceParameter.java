package ch.fluxron.fluxronapp.data;

/**
 * .
 */
public class DeviceParameter {
    private String name;
    private int objectType;
    private int dataType;
    private String accessType;
    private String defaultValue;
    private int pdoMapping;
    private int subNumber;

    private int index;
    private int subindex = 0;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getPdoMapping() {
        return pdoMapping;
    }

    public void setPdoMapping(int pdoMapping) {
        this.pdoMapping = pdoMapping;
    }

    public int getSubNumber() {
        return subNumber;
    }

    public void setSubNumber(int subNumber) {
        this.subNumber = subNumber;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSubindex() {
        return subindex;
    }

    public void setSubindex(int subindex) {
        this.subindex = subindex;
    }

    @Override
    public String toString() {
        return "DeviceParameter{" +
                "name='" + name + '\'' +
                ", objectType=" + objectType +
                ", dataType=" + dataType +
                ", accessType='" + accessType + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", pdoMapping=" + pdoMapping +
                ", subNumber=" + subNumber +
                ", index=" + index +
                ", subindex=" + subindex +
                '}';
    }
}
