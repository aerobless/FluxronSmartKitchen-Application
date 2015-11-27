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

    public CoilConfiguration(String description, int sensorCount, int frequency, int panOn, int panOff, int coilPeakCurrentRated, int pMax) {
        this.description = description;
        this.sensorCount = sensorCount;
        this.frequency = frequency;
        this.panOn = panOn;
        this.panOff = panOff;
        this.coilPeakCurrentRated = coilPeakCurrentRated;
        this.pMax = pMax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getPanOn() {
        return panOn;
    }

    public void setPanOn(int panOn) {
        this.panOn = panOn;
    }

    public int getPanOff() {
        return panOff;
    }

    public void setPanOff(int panOff) {
        this.panOff = panOff;
    }

    public int getCoilPeakCurrentRated() {
        return coilPeakCurrentRated;
    }

    public void setCoilPeakCurrentRated(int coilPeakCurrentRated) {
        this.coilPeakCurrentRated = coilPeakCurrentRated;
    }

    public int getpMax() {
        return pMax;
    }

    public void setpMax(int pMax) {
        this.pMax = pMax;
    }
}
