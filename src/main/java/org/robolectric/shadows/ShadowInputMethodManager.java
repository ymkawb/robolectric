package org.robolectric.shadows;

import android.os.IBinder;
import android.os.ResultReceiver;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import org.robolectric.Robolectric;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;

@Implements(value = InputMethodManager.class, callThroughByDefault = false)
public class ShadowInputMethodManager {

    private boolean softInputVisible;

    @Implementation
    static public InputMethodManager peekInstance() {
        return Robolectric.newInstanceOf(InputMethodManager.class);
    }

//    @Implementation
//    public void focusIn(View view) {
//    }

    @Implementation
    public boolean showSoftInput(View view, int flags) {
        return showSoftInput(view, flags, null);
    }

    @Implementation
    public boolean showSoftInput(View view, int flags, ResultReceiver resultReceiver) {
        softInputVisible = true;
        return true;
    }

    @Implementation
    public boolean hideSoftInputFromWindow(IBinder windowToken, int flags) {
        return hideSoftInputFromWindow(windowToken, flags, null);
    }

    @Implementation
    public boolean hideSoftInputFromWindow(IBinder windowToken, int flags,
                                           ResultReceiver resultReceiver) {
        softInputVisible = false;
        return true;
    }

    public boolean isSoftInputVisible() {
        return softInputVisible;
    }
}