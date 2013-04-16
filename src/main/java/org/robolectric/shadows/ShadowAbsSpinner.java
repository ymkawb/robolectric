package org.robolectric.shadows;

import android.widget.AbsSpinner;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

import static org.robolectric.Robolectric.directlyOn;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(AbsSpinner.class)
public class ShadowAbsSpinner extends ShadowAdapterView {
    @RealObject AbsSpinner realAbsSpinner;
    private boolean animatedTransition;

    @Implementation
    public void setSelection(int position, boolean animate) {
        directlyOn(realAbsSpinner, AbsSpinner.class).setSelection(position);
        animatedTransition = animate;
    }

    // Non-implementation helper method
    public boolean isAnimatedTransition() {
        return animatedTransition;
    }
}
