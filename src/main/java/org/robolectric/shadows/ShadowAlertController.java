package org.robolectric.shadows;

import android.view.View;
import org.robolectric.Robolectric;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

import java.lang.reflect.InvocationTargetException;

import static org.robolectric.Robolectric.directlyOn;

@Implements(value = Robolectric.Anything.class, className = ShadowAlertController.ALERT_CONTROLLER_CLASS_NAME)
public class ShadowAlertController {
    public static final String ALERT_CONTROLLER_CLASS_NAME = "com.android.internal.app.AlertController";
    @RealObject Object realAlertController;

    private CharSequence title;
    private CharSequence message;
    private View view;
    private View customTitleView;

    @Implementation
    public void setTitle(CharSequence title) throws InvocationTargetException, IllegalAccessException {
        this.title = title;
        directlyOn(realAlertController, "com.android.internal.app.AlertController", "setTitle", CharSequence.class)
                .invoke(title);
    }

    public CharSequence getTitle() {
        return title == null ? "" : title;
    }

    @Implementation
    public void setCustomTitle(View customTitleView) {
        this.customTitleView = customTitleView;
        directlyOn(realAlertController, "com.android.internal.app.AlertController", "setCustomTitle", View.class)
                .invoke(customTitleView);
    }

    public View getCustomTitleView() {
        return customTitleView;
    }

    @Implementation
    public void setMessage(CharSequence message) {
        this.message = message;
        directlyOn(realAlertController, "com.android.internal.app.AlertController", "setMessage", CharSequence.class)
                .invoke(message);
    }

    public CharSequence getMessage() {
        return message == null ? "" : message;
    }
    
    @Implementation
    public void setView(View view) {
        this.view = view;
        directlyOn(realAlertController, "com.android.internal.app.AlertController", "setView", View.class)
                .invoke(view);
    }

    public View getView() {
        return view;
    }
}
