package ch.fluxron.generator;

/**
 * DeviceParameter contains information such as index and subindex of a specific parameter.
 *
 * WARNING: This class is specified in buildSrc/java/ch.fluxron.generator.
 * The version in app/java/ch.fluxron.fluxronapp/data/generated is overwritten on build.
 */
public class DeviceParameter {
    private String name;
    private String id;
    private int objectType;
    private int dataType;
    private String accessType;
    private String defaultValue;
    private int subNumber;
    private String deviceClass;

    private byte[] index;
    private byte subindex = 0;

    public DeviceParameter() {
    }

    public DeviceParameter(String name, String id, int objectType, int dataType, String accessType, String defaultValue, int subNumber, byte[] index, byte subindex, String deviceClass) {
        this.name = name;
        this.id = id;
        this.objectType = objectType;
        this.dataType = dataType;
        this.accessType = accessType;
        this.defaultValue = defaultValue;
        this.subNumber = subNumber;
        this.index = index;
        this.subindex = subindex;
        this.deviceClass = deviceClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getSubNumber() {
        return subNumber;
    }

    public void setSubNumber(int subNumber) {
        this.subNumber = subNumber;
    }

    public byte[] getIndex() {
        return index;
    }

    public void setIndex(byte[] index) {
        this.index = index;
    }

    public byte getSubindex() {
        return subindex;
    }

    public void setSubindex(byte subindex) {
        this.subindex = subindex;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }

    @Override
    public String toString() {
        return "DeviceParameter{" +
                "name='" + name + '\'' +
                ", objectType=" + objectType +
                ", dataType=" + dataType +
                ", accessType='" + accessType + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", subNumber=" + subNumber +
                ", index=" + printUnsignedByteArray(index) +
                ", subindex=" + subindex +
                ", deviceClass=" + deviceClass +
                '}';
    }

    private String printUnsignedByteArray(byte[] message) {
        String hexMessage = "";
        for (int i = 0; i < message.length; i++) {
            hexMessage = hexMessage+Integer.toHexString(0xFF & message[i])+" ";
        }
        return hexMessage;
    }
}
