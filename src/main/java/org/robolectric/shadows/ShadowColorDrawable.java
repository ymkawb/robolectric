package org.robolectric.shadows;


import android.graphics.drawable.ColorDrawable;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

@Implements(ColorDrawable.class)
public class ShadowColorDrawable extends ShadowDrawable {
    @RealObject ColorDrawable realColorDrawable;

    @Override @Implementation
    public boolean equals(Object o) {
        if (!(o instanceof ColorDrawable)) return false;
        ColorDrawable other = (ColorDrawable) o;
        if (realColorDrawable == other) return true;
        if (realColorDrawable.getColor() != other.getColor()) return false;
        if (realColorDrawable.getAlpha() != other.getAlpha()) return false;
        if (realColorDrawable.getOpacity() != other.getOpacity()) return false;
        return super.equals(o);
    }

    @Override @Implementation
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + realColorDrawable.getColor();
        result = 31 * result + realColorDrawable.getAlpha();
        result = 31 * result + realColorDrawable.getOpacity();
        return result;
    }

    @Override @Implementation
    public String toString() {
        return "ColorDrawable{color=" + realColorDrawable.getColor() + ", alpha=" + realColorDrawable.getAlpha() + '}';
    }
}
