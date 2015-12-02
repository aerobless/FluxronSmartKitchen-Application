package ch.fluxron.fluxronapp.ui.util;

import android.util.SparseArray;

/**
 * Converts the product code of a device to the device type or the device description.
 */
public class DeviceTypeConverter {
    SparseArray<ProductCode> deviceTypes;
    public static final String CCLASS = "CClass";
    public static final String ACCLASS = "AClass";
    public static final String SCLASS = "SClass";
    public static final String MIA = "MIA";
    public static final String ETX = "ETX";

    /**
     * Instantiates a new DeviceTypeConverter.
     */
    public DeviceTypeConverter() {
        this.deviceTypes = new SparseArray<>();
        init();
    }

    /**
     * Get the Device Type. E.g. BAX.
     *
     * @param input
     * @return
     */
    public String toDeviceClass(int input) {
        return deviceTypes.get(input).getDeviceClass();
    }

    /**
     * Get the Device Type. E.g. BAX.
     *
     * @param input
     * @return
     */
    public String toDeviceType(int input) {
        return deviceTypes.get(input).getType();
    }

    /**
     * Get the Device Description. E.g. 3500-C.
     *
     * @param input
     * @return
     */
    public String toDeviceDescription(int input) {
        return deviceTypes.get(input).getDescription();
    }

    /**
     * Initializes this DeviceTypeConverter.
     */
    private void init() {
        deviceTypes.append(15460, new ProductCode("LOH", "Modul", "CClass"));
        deviceTypes.append(15381, new ProductCode("LOH", "Flex", "CClass"));
        deviceTypes.append(15373, new ProductCode("LOH", "3500-C", "CClass"));
        deviceTypes.append(15374, new ProductCode("LOH", "3500-S", "SClass"));
        deviceTypes.append(15378, new ProductCode("LOH", "8000-S", "SClass"));
        deviceTypes.append(2573, new ProductCode("BAX", "3500-C", "CClass"));
        deviceTypes.append(2574, new ProductCode("BAX", "3500-S", "SClass"));
        deviceTypes.append(2575, new ProductCode("BAX", "5000-C", "CClass"));
        deviceTypes.append(2576, new ProductCode("BAX", "5000-S", "SClass"));
        deviceTypes.append(2577, new ProductCode("BAX", "8000-C", "CClass"));
        deviceTypes.append(2578, new ProductCode("BAX", "8000-S", "SClass"));

        deviceTypes.append(12801, new ProductCode("REX", "3500-A", "AClass"));
        deviceTypes.append(12802, new ProductCode("REX", "5000-A", "AClass"));
        deviceTypes.append(12813, new ProductCode("REX", "3500-C", "CClass"));
        deviceTypes.append(12814, new ProductCode("REX", "3500-S", "SClass"));
        deviceTypes.append(12815, new ProductCode("REX", "5000-C", "CClass"));
        deviceTypes.append(12816, new ProductCode("REX", "5000-S", "SClass"));
        deviceTypes.append(12817, new ProductCode("REX", "8000-C", "CClass"));
        deviceTypes.append(12818, new ProductCode("REX", "8000-S", "SClass"));
        deviceTypes.append(12819, new ProductCode("REX", "2x5000-C", "CClass"));
        deviceTypes.append(12820, new ProductCode("REX", "2x5000-S", "SClass"));
        deviceTypes.append(12821, new ProductCode("REX", "2x8000-C", "CClass"));
        deviceTypes.append(12822, new ProductCode("REX", "2x8000-S", "SClass"));

        deviceTypes.append(13067, new ProductCode("LIFT", "C235-3.5", "CClass"));
        deviceTypes.append(13068, new ProductCode("LIFT", "Q235-5", "CClass"));
        deviceTypes.append(13069, new ProductCode("LIFT", "C235-5", "CClass"));
        deviceTypes.append(13077, new ProductCode("LIFT", "W300-3.5", "CClass"));
        deviceTypes.append(13079, new ProductCode("LIFT", "W300-5.0", "CClass"));
        deviceTypes.append(13083, new ProductCode("LIFT", "W300-8.0", "CClass"));
        deviceTypes.append(13087, new ProductCode("LIFT", "C270-3.5", "CClass"));
        deviceTypes.append(13088, new ProductCode("LIFT", "Q270-5.0", "CClass"));
        deviceTypes.append(13089, new ProductCode("LIFT", "C270-5.0", "CClass"));
        deviceTypes.append(13090, new ProductCode("LIFT", "2R270-5.0", "CClass"));
        deviceTypes.append(13091, new ProductCode("LIFT", "4Q130-5.0", "CClass"));
        deviceTypes.append(13093, new ProductCode("LIFT", "C270-8.0", "CClass"));
        deviceTypes.append(13094, new ProductCode("LIFT", "2R270-8.0", "CClass"));
        deviceTypes.append(13095, new ProductCode("LIFT", "4Q130-8.0", "CClass"));
        deviceTypes.append(13098, new ProductCode("LIFT", "Q305-5.0", "CClass"));
        deviceTypes.append(13099, new ProductCode("LIFT", "C305-5.0", "CClass"));
        deviceTypes.append(13101, new ProductCode("LIFT", "2R305-5.0", "CClass"));
        deviceTypes.append(13102, new ProductCode("LIFT", "Q305-8.0", "CClass"));
        deviceTypes.append(13103, new ProductCode("LIFT", "C305-8.0", "CClass"));
        deviceTypes.append(13104, new ProductCode("LIFT", "2R305-8.0", "CClass"));
        deviceTypes.append(13105, new ProductCode("LIFT", "4Q145-8.0", "CClass"));
        deviceTypes.append(13107, new ProductCode("LIFT", "C210-3.5", "CClass"));
        deviceTypes.append(13108, new ProductCode("LIFT", "Q210-5.1", "CClass"));
        deviceTypes.append(13109, new ProductCode("LIFT", "C210-5.0", "CClass"));
        deviceTypes.append(13116, new ProductCode("LIFT", "R185x385-3.5", "CClass"));
        deviceTypes.append(13133, new ProductCode("LIFT", "W400-8.0", "CClass"));
        deviceTypes.append(13145, new ProductCode("LIFT", "4Q160-8.0", "CClass"));
        deviceTypes.append(13155, new ProductCode("LIFT", "4Q180-8.0", "CClass"));

        deviceTypes.append(10296, new ProductCode("MIA", "5600", "MIA"));

        deviceTypes.append(5130, new ProductCode("ET", "10", "ETX"));
        deviceTypes.append(5131, new ProductCode("ET", "100", "ETX"));
        deviceTypes.append(5132, new ProductCode("ET", "1", "ETX"));
        deviceTypes.append(5150, new ProductCode("ET", "300", "ETX"));
        deviceTypes.append(5151, new ProductCode("ET", "3000", "ETX"));
        deviceTypes.append(5152, new ProductCode("ET", "3", "ETX"));

        deviceTypes.append(7780, new ProductCode("ZWG", "100", "CClass"));
        deviceTypes.append(3936, new ProductCode("DGL", "9600", "CClass"));
    }

    /**
     * A value object containing the product code and its properties.
     */
    private class ProductCode {
        String type;
        String description;
        String deviceClass;

        /**
         * Instantiates a new ProductCode.
         *
         * @param type
         * @param description
         * @param deviceClass
         */
        public ProductCode(String type, String description, String deviceClass) {
            this.type = type;
            this.description = description;
            this.deviceClass = deviceClass;
        }

        /**
         * Returns the type of the product code.
         *
         * @return
         */
        public String getType() {
            return type;
        }

        /**
         * Returns the description of the product code.
         *
         * @return
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns the class of the product code.
         *
         * @return
         */
        public String getDeviceClass() {
            return deviceClass;
        }
    }
}
