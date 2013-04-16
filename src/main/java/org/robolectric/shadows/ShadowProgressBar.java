package org.robolectric.shadows;

import android.widget.ProgressBar;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;

@Implements(value = ProgressBar.class)
public class ShadowProgressBar extends ShadowView {

    private int progress;
    private int secondaryProgress;
    private int max = 100;
    private boolean isIndeterminate;

    @Implementation
    public void setMax(int max) {
        this.max = max;
        if (progress > max) {
            progress = max;
        }
    }

    @Implementation
    public int getMax() {
        return max;
    }

    @Implementation
    public void setProgress(int progress) {
        if (!isIndeterminate()) this.progress = Math.min(max, progress);
    }

    @Implementation
    public int getProgress() {
        return isIndeterminate ? 0 : progress;
    }

    @Implementation
    public void setSecondaryProgress(int secondaryProgress) {
        if (!isIndeterminate()) this.secondaryProgress = Math.min(max, secondaryProgress);
    }

    @Implementation
    public int getSecondaryProgress() {
        return isIndeterminate ? 0 : secondaryProgress;
    }

    @Implementation
    public void setIndeterminate(boolean indeterminate) {
        this.isIndeterminate = indeterminate;
    }

    @Implementation
    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    @Implementation
    public void incrementProgressBy(int diff) {
        if (!isIndeterminate()) setProgress(progress + diff);
    }

    @Implementation
    public void incrementSecondaryProgressBy(int diff) {
        if (!isIndeterminate()) setSecondaryProgress(secondaryProgress + diff);
    }
}
