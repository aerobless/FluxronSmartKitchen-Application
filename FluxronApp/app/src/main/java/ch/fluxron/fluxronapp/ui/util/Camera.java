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
     * Gets the scaling factor of this camera
     * @return Scale
     */
    public float getScale() {
        return _scale;
    }

    /**
     * Sets the scaling factor of this camera
     * @param scale Scale
     */
    public void setScale(float scale) {
        this._scale = scale;
        updateTransformMatrix();
    }

    /**
     * Returns the coordinates after the effects of the camera have been reverted.
     * @param x X in view space
     * @param y Y in view space
     * @return Point in world space
     */
    public PointF getAsUntransformedCoordinates(float x, float y)
    {
        float transformedX = x / _scale - _translation.x;
        float transformedY = y / _scale - _translation.y;
        return new PointF(transformedX, transformedY);
    }

    /**
     * Sets the scale and translates relatively to the scale
     * @param scale Scale
     * @param relativeTranslateX Translation x
     * @param relativeTranslateY Translation y
     */
    public void setScaleAndRelativeTranslate(float scale, float relativeTranslateX, float relativeTranslateY)
    {
        _scale = scale;
        translate(relativeTranslateX, relativeTranslateY);
        updateTransformMatrix();
    }

    /**
     * Translates this camera by the delta vector
     * @param relativeTranslateX dx
     * @param relativeTranslateY dy
     */
    private void translate(float relativeTranslateX, float relativeTranslateY)
    {
        PointF currentTranslation = getTranslation();
        setTranslation(currentTranslation.x + relativeTranslateX, currentTranslation.y + relativeTranslateY);
    }

    /**
     * Returns the translation vector
     * @return Translation
     */
    public PointF getTranslation() {
        if (_translation == null) {
            _translation = new PointF();
        }
        return _translation;
    }

    /**
     * Copies the translation into a new point
     * @return Copied point
     */
    public PointF copyTranslation() {
        PointF currentTranslation = getTranslation();
        return new PointF(currentTranslation.x, currentTranslation.y);
    }

    /**
     * Sets the translation of this camera
     * @param translation Translation
     */
    public void setTranslation(PointF translation) {
        this._translation = translation;

        updateTransformMatrix();
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
     * Sets the translation by using the specified scalars
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
     * Sets the transformation Matrix
     * @param transformMatrix Matrix
     */
    public void setTransformMatrix(Matrix transformMatrix) {
        this._transformMatrix = transformMatrix;
    }

    /**
     * Returns the inverse matrix
     * @return Inverse matrix
     */
    public Matrix getInverseMatrix() {
        if(_inverseMatrix == null)
        {
            _inverseMatrixShouldBeRecalculated = true;
            _inverseMatrix = new Matrix();
        }
        if(_inverseMatrixShouldBeRecalculated)
        {
            Matrix transformMatrix = getTransformMatrix();
            if(!transformMatrix.invert(_inverseMatrix))
            {
                _inverseMatrix.reset();
            }
            _inverseMatrixShouldBeRecalculated = false;
        }
        return _inverseMatrix;
    }
}
