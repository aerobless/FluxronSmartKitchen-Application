package ch.fluxron.fluxronapp.objectBase;

/**
 * Used to specify different coil setup configurations.
 */
public class CoilConfiguration {
    String description;
    int sensorCount;
    int frequency;
    int panOn;
    int panOff;
    int coilPeakCurrentRated;
    int pMax;

    /**
     * Instantiates a new CoilConfiguration.
     *
     * @param description
     * @param sensorCount
     * @param frequency
     * @param panOn
     * @param panOff
     * @param coilPeakCurrentRated
     * @param pMax
     */
    public CoilConfiguration(String description, int sensorCount, int frequency, int panOn, int panOff, int coilPeakCurrentRated, int pMax) {
        this.description = description;
        this.sensorCount = sensorCount;
        this.frequency = frequency;
        this.panOn = panOn;
        this.panOff = panOff;
        this.coilPeakCurrentRated = coilPeakCurrentRated;
        this.pMax = pMax;
    }

    /**
     * Returns the description of this CoilConfiguration.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this CoilConfiguration.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the sensorCount of this CoilConfiguration.
     *
     * @return sensorCount
     */
    public int getSensorCount() {
        return sensorCount;
    }

    /**
     * Sets the sensorCount of this CoilConfiguration.
     *
     * @param sensorCount
     */
    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    /**
     * Returns the frequency of this CoilConfiguration.
     *
     * @return frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency of this CoilConfiguration.
     *
     * @param frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns the panOn status of this CoilConfiguration.
     *
     * @return panOn status
     */
    public int getPanOn() {
        return panOn;
    }

    /**
     * Sets the panOn status of this CoilConfiguration.
     *
     * @param panOn
     */
    public void setPanOn(int panOn) {
        this.panOn = panOn;
    }

    /**
     * Returns the panOff status of this CoilConfiguration.
     *
     * @return panOff status
     */
    public int getPanOff() {
        return panOff;
    }

    /**
     * Sets the panOff status of this CoilConfiguration.
     *
     * @param panOff
     */
    public void setPanOff(int panOff) {
        this.panOff = panOff;
    }

    /**
     * Returns the CoilPeakCurrentRated of this CoilConfiguration.
     *
     * @return CoilPeakCurrentRated
     */
    public int getCoilPeakCurrentRated() {
        return coilPeakCurrentRated;
    }

    /**
     * Sets the coilPeakCurrentRated of this CoilConfiguration.
     *
     * @param coilPeakCurrentRated
     */
    public void setCoilPeakCurrentRated(int coilPeakCurrentRated) {
        this.coilPeakCurrentRated = coilPeakCurrentRated;
    }

    /**
     * Returns the pMax of this CoilConfiguration.
     *
     * @return pMax
     */
    public int getpMax() {
        return pMax;
    }

    /**
     * Sets the pMax of this CoilConfiguration.
     *
     * @param pMax
     */
    public void setpMax(int pMax) {
        this.pMax = pMax;
    }
}
