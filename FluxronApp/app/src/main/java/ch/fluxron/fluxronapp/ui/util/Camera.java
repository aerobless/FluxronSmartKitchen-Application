package ch.fluxron.fluxronapp.ui.util;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Camera on a canvas.
 * https://github.com/Consoar/FamilyTree/blob/master/app/src/main/java/bos/whu/familytree/support/views/CanvasCamera.java
 */
public class Camera {

    private float _scale;
    private PointF _translation;
    private Matrix _transformMatrix;
    private Matrix _inverseMatrix;
    private boolean _inverseMatrixShouldBeRecalculated = true;

    /**
     * Creates a new camera
     */
    public Camera() {
        setTransformMatrix(new Matrix());
    }

    /**
     * Gets the scale factor
     * @return Scale factor
     */
    public float getScale() {
        return _scale;
    }

    /**
     * Sets the scale factor
     * @param scale Factor
     */
    public void setScale(float scale) {
        this._scale = scale;
        updateTransformMatrix();
    }

    /**
     * Inverts the transform
     * @param x X
     * @param y Y
     * @return Inverted
     */
    public PointF getAsUntransformedCoordinates(float x, float y)
    {
        float transformedX = x / _scale - _translation.x;
        float transformedY = y / _scale - _translation.y;
        return new PointF(transformedX, transformedY);
    }

    /**
     * Scales the camera view and translate relative to it so it stays centered
     * @param scale Scale
     * @param relativeTranslateX Center offset x
     * @param relativeTranslateY Center offset y
     */
    public void setScaleAndRelativeTranslate(float scale, float relativeTranslateX, float relativeTranslateY)
    {
        _scale = scale;
        translate(relativeTranslateX, relativeTranslateY);
        updateTransformMatrix();
    }

    /**
     * Translates the camera
     * @param relativeTranslateX X
     * @param relativeTranslateY Y
     */
    private void translate(float relativeTranslateX, float relativeTranslateY)
    {
        PointF currentTranslation = getTranslation();
        setTranslation(currentTranslation.x + relativeTranslateX, currentTranslation.y + relativeTranslateY);
    }

    /**
     * Returns the translation vector
     * @return translation vector
     */
    public PointF getTranslation() {
        if (_translation == null) {
            _translation = new PointF();
        }
        return _translation;
    }

    /**
     * Returns a copy of the translation vector
     * @return A copy of the translation vector
     */
    public PointF copyTranslation() {
        PointF currentTranslation = getTranslation();
        return new PointF(currentTranslation.x, currentTranslation.y);
    }

    /**
     * Recalculates the transformation matrix
     */
    public void updateTransformMatrix() {
        Matrix transformMatrix = getTransformMatrix();
        transformMatrix.reset();

        PointF translation = getTranslation();
        transformMatrix.preTranslate(translation.x, translation.y);
        transformMatrix.postScale(_scale, _scale);
    }

    /**
     * Sets the translation
     * @param x X
     * @param y Y
     */
    public void setTranslation(float x, float y) {
        if (_translation == null) {
            _translation = new PointF(x, y);
        } else {
            this._translation.set(x, y);
        }
        updateTransformMatrix();
    }

    /**
     * Gets the transformation matrix
     * @return Transformation matrix
     */
    public Matrix getTransformMatrix() {
        return _transformMatrix;
    }

    /**
     * Sets the transformation matrix
     * @param transformMatrix Transformation matrix
     */
    public void setTransformMatrix(Matrix transformMatrix) {
        this._transformMatrix = transformMatrix;
    }
}
