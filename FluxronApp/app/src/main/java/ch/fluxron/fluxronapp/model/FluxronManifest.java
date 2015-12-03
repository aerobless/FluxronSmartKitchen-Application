package ch.fluxron.fluxronapp.model;

import java.util.Date;

/**
 * Represents the manifest object packed into .fluxron files
 */
public class FluxronManifest {
    private String objectType;
    private String objectName;
    private String objectDescription;
    private String objectId;
    private Date   saveDate;

    /**
     * Returns the object type name
     * @return Object type name
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the object type
     * @param objectType Object type
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the name of the object
     * @return Name of the object
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets the name of the object
     * @param objectName Object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Gets the object description
     * @return Object description
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
     * Sets the description of the object
     * @param objectDescription Object description
     */
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    /**
     * Returns the save date
     * @return Save date
     */
    public Date getSaveDate() {
        return saveDate;
    }

    /**
     * Sets the save date
     * @param saveDate Save date
     */
    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }

    /**
     * Gets the id of the object
     * @return Object id
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the object id
     * @param objectId Id of the object
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
