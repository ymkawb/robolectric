package org.robolectric.shadows;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import org.fest.reflect.core.Reflection;
import org.robolectric.Robolectric;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.robolectric.Robolectric.Reflection.newInstanceOf;
import static org.robolectric.Robolectric.directlyOn;
import static org.robolectric.Robolectric.shadowOf;
import static org.robolectric.bytecode.RobolectricInternals.getConstructor;

/**
 * Shadow implementation of {@code View} that simulates the behavior of this
 * class.
 * <p/>
 * Supports listeners, focusability (but not focus order), resource loading,
 * visibility, onclick, tags, and tracks the size and shape of the view.
 */
@SuppressWarnings({"UnusedDeclaration"})
@Implements(value = View.class, callThroughByDefault = true)
public class ShadowView {
    // This is dumb, we should have a Robolectric-wide way of warning about weird states. todo [xw]
    public static boolean strict = false;

    @RealObject
    protected View realView;

    ShadowView parent;
    protected Context context;
    private boolean selected;
    private boolean pressed;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private Object tag;
    private boolean enabled = true;
    private int visibility = View.VISIBLE;
    int left;
    int top;
    int right;
    int bottom;
    float x;
    float y;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private ViewGroup.LayoutParams layoutParams;
    private Map<Integer, Object> tags = new HashMap<Integer, Object>();
    private boolean clickable;
    private boolean longClickable;
    protected boolean focusable;
    boolean focusableInTouchMode;
    private int backgroundResourceId = -1;
    private int backgroundColor;
    protected View.OnKeyListener onKeyListener;
    private boolean isFocused;
    private View.OnFocusChangeListener onFocusChangeListener;
    private boolean wasInvalidated;
    private View.OnTouchListener onTouchListener;
    protected AttributeSet attributeSet;
    private boolean drawingCacheEnabled;
    public Point scrollToCoordinates = new Point();
    private boolean didRequestLayout;
    private Drawable background;
    private Animation animation;
    private ViewTreeObserver viewTreeObserver;
    private MotionEvent lastTouchEvent;
    private int nextFocusDownId = View.NO_ID;
    private CharSequence contentDescription = null;
    private int measuredWidth = 0;
    private int measuredHeight = 0;
    private TouchDelegate touchDelegate;
    private float translationX = 0.0f;
    private float translationY = 0.0f;
    private float alpha = 1.0f;
    private boolean attachedToWindow;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private int hapticFeedbackPerformed = -1;
    private boolean onLayoutWasCalled;

    public void __constructor__(Context context, AttributeSet attributeSet, int defStyle) {
        if (context == null) throw new NullPointerException("no context");

        this.context = context;
        this.attributeSet = attributeSet;

        getConstructor(View.class, realView, Context.class, AttributeSet.class, int.class)
                .invoke(context, attributeSet, defStyle);

        if (attributeSet != null) {
            applyAttributes();
        }
    }

    public void applyAttributes() {
        applyIdAttribute();
        applyVisibilityAttribute();
        applyEnabledAttribute();
        applyBackgroundAttribute();
        applyTagAttribute();
        applyOnClickAttribute();
        applyContentDescriptionAttribute();

        // todo test
        applyAlphaAttribute();
    }

    @Implementation
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Implementation
    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    /**
     * Also sets focusable in touch mode to false if {@code focusable} is false, which is the Android behavior.
     *
     * @param focusable the new status of the {@code View}'s focusability
     */
    @Implementation
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
        if (!focusable) {
            setFocusableInTouchMode(false);
        }
    }

    @Implementation
    public final boolean isFocusableInTouchMode() {
        return focusableInTouchMode;
    }

    /**
     * Also sets focusable to true if {@code focusableInTouchMode} is true, which is the Android behavior.
     *
     * @param focusableInTouchMode the new status of the {@code View}'s touch mode focusability
     */
    @Implementation
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        this.focusableInTouchMode = focusableInTouchMode;
        if (focusableInTouchMode) {
            setFocusable(true);
        }
    }

    @Implementation(i18nSafe = false)
    public void setContentDescription(CharSequence contentDescription) {
        this.contentDescription = contentDescription;
    }

    @Implementation
    public boolean isFocusable() {
        return focusable;
    }

    @Implementation
    public CharSequence getContentDescription() {
        return contentDescription;
    }

    /**
     * Simulates the inflating of the requested resource.
     *
     * @param context  the context from which to obtain a layout inflater
     * @param resource the ID of the resource to inflate
     * @param root     the {@code ViewGroup} to add the inflated {@code View} to
     * @return the inflated View
     */
    @Implementation
    public static View inflate(Context context, int resource, ViewGroup root) {
        return ShadowLayoutInflater.from(context).inflate(resource, root);
    }

//    /**
//     * Finds this {@code View} if it's ID is passed in, returns {@code null} otherwise
//     *
//     * @param id the id of the {@code View} to find
//     * @return the {@code View}, if found, {@code null} otherwise
//     */
//    @Implementation
//    public View findViewById(int id) {
//        if (id == this.id) {
//            return realView;
//        }
//
//        return null;
//    }

    @Implementation
    public View findViewWithTag(Object obj) {
        if (obj.equals(realView.getTag())) {
            return realView;
        }

        return null;
    }

    @Implementation
    public View getRootView() {
        ShadowView root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        return root.realView;
    }

    @Implementation
    public ViewGroup.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    @Implementation
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        layoutParams = params;
    }

//    @Implementation
//    public final ViewParent getParent() {
//        return parent == null ? null : (ViewParent) parent.realView;
//    }

    @Implementation
    public final Context getContext() {
        return context;
    }

    @Implementation
    public Resources getResources() {
        return context.getResources();
    }

    /**
     * Build drawable, either LayerDrawable or BitmapDrawable.
     *
     * @param resourceId Resource id
     * @return Drawable
     */
    protected Drawable buildDrawable(int resourceId) {
        return getResources().getDrawable(resourceId);
    }

    protected String getQualifiers() {
        return shadowOf(context.getResources().getConfiguration()).getQualifiers();
    }

    @Implementation
    public void setBackgroundResource(int backgroundResourceId) {
        this.backgroundResourceId = backgroundResourceId;
        setBackgroundDrawable(backgroundResourceId == 0 ? null : getResources().getDrawable(backgroundResourceId));
    }

    /**
     * Non-Android accessor.
     *
     * @return the resource ID of this views background
     */
    public int getBackgroundResourceId() {
        return backgroundResourceId;
    }

    @Implementation
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        setBackgroundDrawable(new ColorDrawable(color));
    }

    /**
     * Non-Android accessor.
     *
     * @return the resource color ID of this views background
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Implementation
    public void setBackground(Drawable d) {
        setBackgroundDrawable(d);
    }

    @Implementation
    public void setBackgroundDrawable(Drawable d) {
        this.background = d;
    }

    @Implementation
    public Drawable getBackground() {
        return background;
    }

    @Implementation
    public int getVisibility() {
        return visibility;
    }

    @Implementation
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Implementation
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Implementation
    public boolean isSelected() {
        return this.selected;
    }

    @Implementation
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Implementation
    public boolean isPressed() {
        return this.pressed;
    }

    @Implementation
    public boolean isEnabled() {
        return this.enabled;
    }

    @Implementation
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Implementation
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        if (!isClickable()) {
            setClickable(true);
        }
    }

    @Implementation
    public boolean performClick() {
        if (onClickListener != null) {
            onClickListener.onClick(realView);
            return true;
        } else {
            return false;
        }
    }

    @Implementation
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        if (!isLongClickable()) {
            setLongClickable(true);
        }
    }

    @Implementation
    public boolean performLongClick() {
        if (onLongClickListener != null) {
            onLongClickListener.onLongClick(realView);
            return true;
        } else {
            return false;
        }
    }

    @Implementation
    public void setOnKeyListener(View.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    @Implementation
    public Object getTag() {
        return this.tag;
    }

    @Implementation
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Implementation
    public final int getHeight() {
        return bottom - top;
    }

    @Implementation
    public final int getWidth() {
        return right - left;
    }

    @Implementation
    public final int getMeasuredWidth() {
        return measuredWidth;
    }

    @Implementation
    public final int getMeasuredHeight() {
        return measuredHeight;
    }

    @Implementation
    public final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        this.measuredWidth = measuredWidth;
        this.measuredHeight = measuredHeight;
    }

    @Implementation
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Implementation
    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        // We really want to invoke the onMeasure method of the real view,
        // as the real View likely contains an implementation of onMeasure
        // worthy of test, rather the default shadow implementation.
        // But Android declares onMeasure as protected.
        try {
            Method onMeasureMethod = realView.getClass().getDeclaredMethod("onMeasure", Integer.TYPE, Integer.TYPE);
            onMeasureMethod.setAccessible(true);
            onMeasureMethod.invoke(realView, widthMeasureSpec, heightMeasureSpec);
        } catch (NoSuchMethodException e) {
            // use default shadow implementation
            onMeasure(widthMeasureSpec, heightMeasureSpec);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Implementation
    public void draw(android.graphics.Canvas canvas) {
        if (background != null) {
            shadowOf(canvas).appendDescription("background:");
            background.draw(canvas);
        }
    }

    @Implementation
    public void layout(int l, int t, int r, int b) {
        if (l != left || r != right || t != top || b != bottom) {
            left = l;
            top = t;
            right = r;
            bottom = b;

            realView.invalidate();
            Reflection.method("onLayout").withParameterTypes(boolean.class, int.class, int.class, int.class, int.class)
                    .in(realView).invoke(true, l, t, r, b);
        }
    }

    @Implementation
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        onLayoutWasCalled = true;
    }

    public boolean onLayoutWasCalled() {
        return onLayoutWasCalled;
    }

    @Implementation
    public void setPadding(int left, int top, int right, int bottom) {
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
    }

    @Implementation
    public int getPaddingTop() {
        return paddingTop;
    }

    @Implementation
    public int getPaddingLeft() {
        return paddingLeft;
    }

    @Implementation
    public int getPaddingRight() {
        return paddingRight;
    }

    @Implementation
    public int getPaddingBottom() {
        return paddingBottom;
    }

    @Implementation
    public Object getTag(int key) {
        return tags.get(key);
    }

    @Implementation
    public void setTag(int key, Object value) {
        tags.put(key, value);
    }

    @Implementation
    public void requestLayout() {
        didRequestLayout = true;
        directlyOn(realView, View.class).requestLayout();
    }

    public boolean didRequestLayout() {
        return didRequestLayout;
    }

    public void setDidRequestLayout(boolean didRequestLayout) {
        this.didRequestLayout = didRequestLayout;
    }

    @Implementation
    public final boolean requestFocus() {
        return requestFocus(View.FOCUS_DOWN);
    }

    @Implementation
    public final boolean requestFocus(int direction) {
        setViewFocus(true);
        return true;
    }

    public void setViewFocus(boolean hasFocus) {
        this.isFocused = hasFocus;
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(realView, hasFocus);
        }
    }

    @Implementation
    public int getNextFocusDownId() {
        return nextFocusDownId;
    }

    @Implementation
    public void setNextFocusDownId(int nextFocusDownId) {
        this.nextFocusDownId = nextFocusDownId;
    }

    @Implementation
    public boolean isFocused() {
        return isFocused;
    }

    @Implementation
    public boolean hasFocus() {
        return isFocused;
    }

    @Implementation
    public void clearFocus() {
        setViewFocus(false);
    }

    @Implementation
    public void setOnFocusChangeListener(View.OnFocusChangeListener listener) {
        onFocusChangeListener = listener;
    }

    @Implementation
    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return onFocusChangeListener;
    }

    @Implementation
    public void invalidate() {
        wasInvalidated = true;
        directlyOn(realView, View.class).invalidate();
    }

    @Implementation
    public boolean onTouchEvent(MotionEvent event) {
        lastTouchEvent = event;
        return false;
    }

    @Implementation
    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    @Implementation
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (onTouchListener != null && onTouchListener.onTouch(realView, event)) {
            return true;
        }
        return realView.onTouchEvent(event);
    }

    public MotionEvent getLastTouchEvent() {
        return lastTouchEvent;
    }

    @Implementation
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (onKeyListener != null) {
            return onKeyListener.onKey(realView, event.getKeyCode(), event);
        }
        return false;
    }

    @Implementation
    public boolean isShown() {
        View parent = realView;
        while (parent != null) {
            if (parent.getVisibility() != View.VISIBLE) {
                return false;
            }
            parent = (View) parent.getParent();
        }
        return true;
    }


    /**
     * Returns a string representation of this {@code View}. Unless overridden, it will be an empty string.
     * <p/>
     * Robolectric extension.
     */
    public String innerText() {
        return "";
    }

    /**
     * Dumps the status of this {@code View} to {@code System.out}
     */
    public void dump() {
        dump(System.out, 0);
    }

    /**
     * Dumps the status of this {@code View} to {@code System.out} at the given indentation level
     */
    public void dump(PrintStream out, int indent) {
        dumpFirstPart(out, indent);
        out.println("/>");
    }

    protected void dumpFirstPart(PrintStream out, int indent) {
        dumpIndent(out, indent);

        out.print("<" + realView.getClass().getSimpleName());
        dumpAttributes(out);
    }

    protected void dumpAttributes(PrintStream out) {
        if (realView.getId() > 0) {
            dumpAttribute(out, "id", shadowOf(context).getResourceLoader().getNameForId(realView.getId()));
        }

        switch (realView.getVisibility()) {
            case View.VISIBLE:
                break;
            case View.INVISIBLE:
                dumpAttribute(out, "visibility", "INVISIBLE");
                break;
            case View.GONE:
                dumpAttribute(out, "visibility", "GONE");
                break;
        }
    }

    protected void dumpAttribute(PrintStream out, String name, String value) {
        out.print(" " + name + "=\"" + (value == null ? null : TextUtils.htmlEncode(value)) + "\"");
    }

    protected void dumpIndent(PrintStream out, int indent) {
        for (int i = 0; i < indent; i++) out.print(" ");
    }

    /**
     * @return left side of the view
     */
    @Implementation
    public int getLeft() {
        return left;
    }

    /**
     * @return top coordinate of the view
     */
    @Implementation
    public int getTop() {
        return top;
    }

    /**
     * @return right side of the view
     */
    @Implementation
    public int getRight() {
        return right;
    }

    /**
     * @return bottom coordinate of the view
     */
    @Implementation
    public int getBottom() {
        return bottom;
    }

    /**
     * @return whether the view is clickable
     */
    @Implementation
    public boolean isClickable() {
        return clickable;
    }

    /**
     * @return whether the view is long-clickable
     */
    @Implementation
    public boolean isLongClickable() {
        return longClickable;
    }

    /**
     * Non-Android accessor.
     *
     * @return whether or not {@link #invalidate()} has been called
     */
    public boolean wasInvalidated() {
        return wasInvalidated;
    }

    /**
     * Clears the wasInvalidated flag
     */
    public void clearWasInvalidated() {
        wasInvalidated = false;
    }

    @Implementation
    public void setLeft(int left) {
        this.left = left;
    }

    @Implementation
    public void setTop(int top) {
        this.top = top;
    }

    @Implementation
    public void setRight(int right) {
        this.right = right;
    }

    @Implementation
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    @Implementation
    public void setX(float newX) {
        this.x = newX;
    }

    @Implementation
    public void setY(float newY) {
        this.y = newY;
    }

    @Implementation
    public float getX() {
        return this.x;
    }

    @Implementation
    public float getY() {
        return this.y;
    }

    /**
     * Non-Android accessor.
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * Non-Android accessor.
     */
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * Non-Android accessor.
     */
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * Non-Android accessor.
     */
    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * Non-Android accessor.
     */
    public void setFocused(boolean focused) {
        isFocused = focused;
    }


    /**
     * Utility method for clicking on views exposing testing scenarios that are not possible when using the actual app.
     *
     * @throws RuntimeException if the view is disabled or if the view or any of its parents are not visible.
     */
    public boolean checkedPerformClick() {
        if (!isShown()) {
            throw new RuntimeException("View is not visible and cannot be clicked");
        }
        if (!realView.isEnabled()) {
            throw new RuntimeException("View is not enabled and cannot be clicked");
        }

        return realView.performClick();
    }

    public void applyFocus() {
        if (noParentHasFocus(realView)) {
            Boolean focusRequested = attributeSet.getAttributeBooleanValue("android", "focus", false);
            if (focusRequested || realView.isFocusableInTouchMode()) {
                realView.requestFocus();
            }
        }
    }

    private void applyIdAttribute() {
        Integer id = attributeSet.getAttributeResourceValue("android", "id", 0);
        if (realView.getId() == 0) {
            realView.setId(id);
        }
    }

    private void applyTagAttribute() {
        Object tag = attributeSet.getAttributeValue("android", "tag");
        if (tag != null) {
            setTag(tag);
            directlyOn(realView, View.class).setTag(tag);
        }
    }

    private void applyVisibilityAttribute() {
        String visibility = attributeSet.getAttributeValue("android", "visibility");
        if (visibility != null) {
            if (visibility.equals("gone")) {
                setVisibility(View.GONE);
            } else if (visibility.equals("invisible")) {
                setVisibility(View.INVISIBLE);
            }
        }
    }

    private void applyEnabledAttribute() {
        setEnabled(attributeSet.getAttributeBooleanValue("android", "enabled", true));
    }

    private void applyBackgroundAttribute() {
        String source = attributeSet.getAttributeValue("android", "background");
        if (source != null) {
            if (source.startsWith("@drawable/")) {
                setBackgroundResource(attributeSet.getAttributeResourceValue("android", "background", 0));
            }
        }
    }

    private void applyOnClickAttribute() {
        final String handlerName = attributeSet.getAttributeValue("android", "onClick");
        if (handlerName == null) {
            return;
        }

        /* good part of following code has been directly copied from original
         * android source */
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Method mHandler;
                try {
                    mHandler = getContext().getClass().getMethod(handlerName,
                            View.class);
                } catch (NoSuchMethodException e) {
                    int id = realView.getId();
                    String idText = id == View.NO_ID ? "" : " with id '"
                            + shadowOf(context).getResourceLoader()
                            .getNameForId(id) + "'";
                    throw new IllegalStateException("Could not find a method " +
                            handlerName + "(View) in the activity "
                            + getContext().getClass() + " for onClick handler"
                            + " on view " + realView.getClass() + idText, e);
                }

                try {
                    mHandler.invoke(getContext(), realView);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not execute non "
                            + "public method of the activity", e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException("Could not execute "
                            + "method of the activity", e);
                }
            }
        });
    }

    private void applyAlphaAttribute() {
        Float alpha = attributeSet.getAttributeFloatValue("android", "alpha", 1f);
        if (alpha != null) {
            setAlpha(alpha);
        }
    }

    private void applyContentDescriptionAttribute() {
        String contentDescription = attributeSet.getAttributeValue("android", "contentDescription");
        if (contentDescription != null) {
            if (contentDescription.startsWith("@string/")) {
                int resId = attributeSet.getAttributeResourceValue("android", "contentDescription", 0);
                contentDescription = context.getResources().getString(resId);
            }
            setContentDescription(contentDescription);
        }
    }

    private boolean noParentHasFocus(View view) {
        while (view != null) {
            if (view.hasFocus()) return false;
            view = (View) view.getParent();
        }
        return true;
    }

    /**
     * Non-android accessor.  Returns touch listener, if set.
     *
     * @return
     */
    public View.OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    /**
     * Non-android accessor.  Returns click listener, if set.
     *
     * @return
     */
    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    /**
     * Non-android accessor.  Returns long click listener, if set.
     *
     * @return
     */
    public View.OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    @Implementation
    public void setDrawingCacheEnabled(boolean drawingCacheEnabled) {
        this.drawingCacheEnabled = drawingCacheEnabled;
    }

    @Implementation
    public boolean isDrawingCacheEnabled() {
        return drawingCacheEnabled;
    }

    @Implementation
    public Bitmap getDrawingCache() {
        return Robolectric.newInstanceOf(Bitmap.class);
    }

    @Implementation
    public void post(Runnable action) {
        Robolectric.getUiThreadScheduler().post(action);
    }

    @Implementation
    public void postDelayed(Runnable action, long delayMills) {
        Robolectric.getUiThreadScheduler().postDelayed(action, delayMills);
    }

    @Implementation
    public void postInvalidateDelayed(long delayMilliseconds) {
        Robolectric.getUiThreadScheduler().postDelayed(new Runnable() {
            @Override
            public void run() {
                realView.invalidate();
            }
        }, delayMilliseconds);
    }

    @Implementation
    public Animation getAnimation() {
        return animation;
    }

    @Implementation
    public void setAnimation(Animation anim) {
        animation = anim;
    }

    @Implementation
    public void startAnimation(Animation anim) {
        setAnimation(anim);
        animation.start();
    }

    @Implementation
    public void clearAnimation() {
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    @Implementation
    public void scrollTo(int x, int y) {
        try {
            Method method = View.class.getDeclaredMethod("onScrollChanged", new Class[]{int.class, int.class, int.class, int.class});
            method.setAccessible(true);
            method.invoke(realView, x, y, scrollToCoordinates.x, scrollToCoordinates.y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        scrollToCoordinates = new Point(x, y);
    }

    @Implementation
    public int getScrollX() {
        return scrollToCoordinates != null ? scrollToCoordinates.x : 0;
    }

    @Implementation
    public int getScrollY() {
        return scrollToCoordinates != null ? scrollToCoordinates.y : 0;
    }

    @Implementation
    public void setScrollX(int scrollX){
        scrollTo(scrollX, scrollToCoordinates.y);
    }

    @Implementation
    public void setScrollY(int scrollY){
        scrollTo(scrollToCoordinates.x, scrollY);
    }

    @Implementation
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    @Implementation
    public float getScaleX() {
        return scaleX;
    }

    @Implementation
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    @Implementation
    public float getScaleY() {
        return scaleY;
    }

    @Implementation
    public ViewTreeObserver getViewTreeObserver() {
        if (viewTreeObserver == null) {
            viewTreeObserver = newInstanceOf(ViewTreeObserver.class);
        }
        return viewTreeObserver;
    }

    @Implementation
    public void onAnimationEnd() {
    }

    @Implementation
    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    @Implementation
    public float getTranslationX() {
        return translationX;
    }

    @Implementation
    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }

    @Implementation
    public float getTranslationY() {
        return translationY;
    }

    @Implementation
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Implementation
    public float getAlpha() {
        return alpha;
    }

    /*
     * Non-Android accessor.
     */
    public void finishedAnimation() {
        try {
            Method onAnimationEnd = realView.getClass().getDeclaredMethod("onAnimationEnd", new Class[0]);
            onAnimationEnd.setAccessible(true);
            onAnimationEnd.invoke(realView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Implementation
    public void setTouchDelegate(TouchDelegate delegate) {
        this.touchDelegate = delegate;
    }

    @Implementation
    public TouchDelegate getTouchDelegate()  {
    	return touchDelegate;
    }

    @Implementation
    public void onAttachedToWindow() {
        if (strict && attachedToWindow) throw new IllegalStateException("already attached!");
        attachedToWindow = true;
    }

    @Implementation
    public void onDetachedFromWindow() {
        if (strict && !attachedToWindow) throw new IllegalStateException("not attached!");
        attachedToWindow = false;
    }

    public boolean isAttachedToWindow() {
        return attachedToWindow;
    }

    public void callOnAttachedToWindow() {
        invokeReflectively("onAttachedToWindow");
    }

    public void callOnDetachedFromWindow() {
        invokeReflectively("onDetachedFromWindow");
    }

    private void invokeReflectively(String methodName) {
        try {
            Method method = View.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(realView);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Implementation
    public boolean performHapticFeedback(int hapticFeedbackType) {
        hapticFeedbackPerformed = hapticFeedbackType;
        return true;
    }

    public int lastHapticFeedbackPerformed() {
        return hapticFeedbackPerformed;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    /**
     * Shadow for {@code View.MeasureSpec} inner class.
     *
     * As the implementation is very simple, it is taken from the AOSP source.
     */
    @Implements(MeasureSpec.class)
    public static class ShadowMeasureSpec {

        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK = 0x3 << MODE_SHIFT;

        @Implementation
        public static int getMode (int measureSpec) {
            return (measureSpec & MODE_MASK);
        }

        @Implementation
        public static int getSize (int measureSpec) {
            return (measureSpec & ~MODE_MASK);
        }

        @Implementation
        public static int makeMeasureSpec (int size, int mode) {
            return size + mode;
        }

        @Implementation
        public static String toString (int measureSpec) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);

            StringBuilder sb = new StringBuilder("MeasureSpec: ");

            if (mode == MeasureSpec.UNSPECIFIED)
                sb.append("UNSPECIFIED ");
            else if (mode == MeasureSpec.EXACTLY)
                sb.append("EXACTLY ");
            else if (mode == MeasureSpec.AT_MOST)
                sb.append("AT_MOST ");
            else
                sb.append(mode).append(" ");

            sb.append(size);
            return sb.toString();
        }

    }
}
