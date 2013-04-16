package org.robolectric.shadows;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;

import static org.robolectric.Robolectric.shadowOf;

@Implements(value = ImageView.class)
public class ShadowImageView extends ShadowView {
    private Drawable imageDrawable;
    private Bitmap imageBitmap;
    private ImageView.ScaleType scaleType;
    private Matrix matrix;
    private int imageLevel;

    @Implementation
    public void setImageBitmap(Bitmap imageBitmap) {
        setImageDrawable(new BitmapDrawable(imageBitmap));
        this.imageBitmap = imageBitmap;
    }

    @Deprecated
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    @Implementation
    public void setImageDrawable(Drawable drawable) {
        this.imageDrawable = drawable;
    }

    @Implementation
    public void setImageResource(int resId) {
        setImageDrawable(buildDrawable(resId));
    }

    @Implementation
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    @Implementation
    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Implementation
    public Drawable getDrawable() {
        return imageDrawable;
    }

    @Implementation
    public void setImageMatrix(Matrix matrix) {
        this.matrix = new Matrix(matrix);
    }

    @Implementation
    public void draw(Canvas canvas) {
        if (matrix != null) {
            canvas.translate(shadowOf(matrix).getTransX(), shadowOf(matrix)
                    .getTransY());
            canvas.scale(shadowOf(matrix).getScaleX(), shadowOf(matrix)
                    .getScaleY());
        }
        if (imageDrawable != null) {
            imageDrawable.draw(canvas);
        }
    }

    private void applyImageAttribute() {
        String source = attributeSet.getAttributeValue("android", "src");
        if (source != null) {
            if (source.startsWith("@drawable/")) {
                setImageResource(attributeSet.getAttributeResourceValue(
                        "android", "src", 0));
            }
        }
    }

    @Implementation
    public void setImageLevel(int imageLevel) {
        this.imageLevel = imageLevel;
    }

    /**
     * Non-Android accessor.
     *
     * @return the imageLevel set in {@code setImageLevel(int)}
     */
    public int getImageLevel() {
        return imageLevel;
    }
}
