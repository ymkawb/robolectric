package org.robolectric.shadows;

import android.os.Looper;
import android.view.Choreographer;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

@Implements(Choreographer.class)
public class ShadowChoreographer {
    @RealObject Choreographer choreographer;
    private Looper looper;

//    public void __constructor__(Looper looper) {
//        this.looper = looper;
//        field("mLock").ofType(Object.class).in(choreographer).set(new Object());
//    }

//    @Implementation
//    public void postCallbackDelayedInternal(int callbackType, Object action, Object token, long delayMillis) {
//        new Handler(looper).postDelayed((Runnable) action, delayMillis);
//    }
}
