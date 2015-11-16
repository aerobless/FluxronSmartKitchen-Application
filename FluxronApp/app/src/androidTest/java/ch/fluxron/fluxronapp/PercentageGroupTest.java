package ch.fluxron.fluxronapp;

import junit.framework.TestCase;

import ch.fluxron.fluxronapp.ui.util.PercentageGroup;

/**
 * Unit tests for the PercentageGroup class
 */
public class PercentageGroupTest extends TestCase {


    public void testEmptyGroupTotal() {
        PercentageGroup grp = new PercentageGroup();
        int total = grp.getTotal();

        assertEquals(0, total);
    }

    public void testEmptyGroupPercentage() {
        PercentageGroup grp = new PercentageGroup();
        float percentage = grp.getPercentageOfTotal(20);

        assertEquals(0, percentage, PercentageGroup.PERCENTAGE_ACCURACY);
    }

    public void testSingleValueTotal() {
        PercentageGroup grp = new PercentageGroup();
        grp.put("testkey", 120);
        int p = grp.getTotal();

        assertEquals(120, p);
    }

    public void testSingleValuePercentage() {
        PercentageGroup grp = new PercentageGroup();
        grp.put("testkey", 890);
        float p = grp.getPercentageOfTotal(70);

        assertEquals(0.07865, p, PercentageGroup.PERCENTAGE_ACCURACY);
    }

    public void testMultipleValuesTotal() {
        PercentageGroup grp = new PercentageGroup();
        grp.put("testkey1", 2);
        grp.put("testkey2", 7);
        grp.put("testkey3", 3);
        grp.put("testkey4", 15);
        int total = grp.getTotal();

        assertEquals(27, total);
    }

    public void testMultipleValuesOverlapTotal() {
        PercentageGroup grp = new PercentageGroup();
        grp.put("testkey1", 2);
        grp.put("testkey2", 7);
        grp.put("testkey3", 3);
        grp.put("testkey1", 15);
        int total = grp.getTotal();

        assertEquals(25, total);
    }

    public void testMultipleValuesPercentage() {
        PercentageGroup grp = new PercentageGroup();
        grp.put("testkey1", 2);
        grp.put("testkey2", 7);
        grp.put("testkey3", 3);
        grp.put("testkey4", 15);
        float percentage = grp.getPercentageOfTotal("testkey1");

        assertEquals(0.07407, percentage, PercentageGroup.PERCENTAGE_ACCURACY);
    }

    public void testMultipleValuesOverlapPercentage() {
        PercentageGroup grp = new PercentageGroup();
        grp.put("testkey1", 2);
        grp.put("testkey2", 7);
        grp.put("testkey3", 3);
        grp.put("testkey1", 15);
        float percentage = grp.getPercentageOfTotal("testkey1");

        assertEquals(0.6, percentage, PercentageGroup.PERCENTAGE_ACCURACY);
    }
}
