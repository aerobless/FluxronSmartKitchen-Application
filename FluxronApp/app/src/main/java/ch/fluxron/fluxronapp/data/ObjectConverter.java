package ch.fluxron.fluxronapp.data;

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
}
