package ch.fluxron.fluxronapp.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Provides utility for object to database conversions
 */
public class ObjectConverter {

    /**
     * Converts an object hierarchy to a map.
     * @param o Object at hierarchy root
     * @return Map containing of an associative map of property = value
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertObjectToMap(Object o){
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(o, Map.class);
    }

    /**
     * Creates the class instance from a fully qualified canonical name
     * @param name Name of the class
     * @return Class instance
     */
    public Class<?> getClassFromName(String name){
        Class<?> result = null;

        try {
            result = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Converts a map to an object using Jackson respecting attributes
     * @param map Map with key-value pairs
     * @return Converted object
     */
    public Object convertMapToObject(Map<String, Object> map, Class<?> targetClass){
        Object result;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        result = mapper.convertValue(map, targetClass);

        return result;
    }
}
