package ch.fluxron.fluxronapp.ui.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a group of values considered to be whole
 */
public class PercentageGroup {
    public static float PERCENTAGE_ACCURACY = 0.00001f;

    private Map<String, Integer> values = new HashMap<>();
    private int total;

    /**
     * Adds or adjusts a value
     * @param key Key of the value
     * @param value Value
     */
    public void put(String key, int value) {
        if (value < 0) throw new IllegalArgumentException("Value must be >= 0!");

        values.put(key, value);
        recalculateTotal();
    }

    /**
     * Returns the sum of all values
     * @return Sum of all values
     */
    public int getTotal() {
        return total;
    }

    /**
     * Returns how much the value is expressed as part of the total of this group
     * @param part Number which should be expressed as a percentage
     * @return Percentage on the 0-1 scale (can be bigger than 1)
     */
    public float getPercentageOfTotal(int part) {
        if (total < 1) return 0;
        else           return (float)part / (float)total;
    }

    /**
     * Returns how much the value is expressed as part of the total of this group
     * @param key Key of the value in this group
     * @return Percentage on the 0-1 scale (can be bigger than 1)
     */
    public float getPercentageOfTotal(String key) {
        return getPercentageOfTotal(values.get(key));
    }

    /**
     * Recalculate the total sum of all values
     */
    private void recalculateTotal() {
        total = 0;
        for(Integer f : values.values()) {
            total += f;
        }
    }
}
