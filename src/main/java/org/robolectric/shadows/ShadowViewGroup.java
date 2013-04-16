package org.robolectric.shadows;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

import java.io.PrintStream;

import static org.robolectric.Robolectric.shadowOf;

/**
 * Shadow for {@code ViewGroup} that simulates its implementation
 */
@SuppressWarnings({"UnusedDeclaration"})
@Implements(ViewGroup.class)
public class ShadowViewGroup extends ShadowView {
    @RealObject protected ViewGroup realViewGroup;

    private AnimationListener animListener;
    private LayoutAnimationController layoutAnim;
    private boolean disallowInterceptTouchEvent = false;
    private MotionEvent interceptedTouchEvent;
//    private ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener;

//    @Implementation
//    @Override
//    public View findViewById(int id) {
//        if (id == getId()) {
//            return realView;
//        }
//
//        for (View child : children) {
//            View found = child.findViewById(id);
//            if (found != null) {
//                return found;
//            }
//        }
//        return null;
//    }

    //@Implementation
    //@Override
    //public View findViewWithTag(Object obj) {
    //    if (obj.equals(realView.getTag())) {
    //        return realView;
    //    }
    //
    //    for (int i = 0; i < realViewGroup.getChildCount(); i++) {
    //        View child = realViewGroup.getChildAt(i);
    //        View found = child.findViewWithTag(obj);
    //        if (found != null) {
    //            return found;
    //        }
    //    }
    //
    //    return null;
    //}

//    @Implementation
//    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
//        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }

//    @Implementation
//    public void addView(View child) {
//        if (child.getParent() != null) {
//            throw new IllegalStateException("The specified child already has a parent. You must call removeView() " +
//                    "on the child's parent first.");
//        }
//        realViewGroup.addView(child, -1);
//        requestLayout();
//    }
//
//    @Implementation
//    public void addView(View child, int index) {
//        ViewGroup.LayoutParams params = child.getLayoutParams();
//        if (params == null) {
//            params = generateDefaultLayoutParams();
//            if (params == null) {
//                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
//            }
//        }
//        ((ViewGroup)realView).addView(child, index, params);
//    }
//
//    @Implementation
//    public void addView(View child, int width, int height) {
//        final ViewGroup.LayoutParams params = generateDefaultLayoutParams();
//        params.width = width;
//        params.height = height;
//
//        realViewGroup.addView(child, -1, params);
//    }
//
//    @Implementation
//    public void addView(View child, ViewGroup.LayoutParams params) {
//        realViewGroup.addView(child, -1, params);
//    }
//
//    @Implementation
//    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//        if (child == realView) throw new RuntimeException("why are you adding me as my own child!?! you got some problems.");
//
//        child.setLayoutParams(params);
//        if (index == -1) {
//            children.add(child);
//        } else {
//            children.add(index, child);
//        }
//        shadowOf(child).parent = this;
//
//        if (isAttachedToWindow()) shadowOf(child).callOnAttachedToWindow();
//        requestLayout();
//    }

//    @Implementation
//    public int indexOfChild(View child) {
//        int count = getChildCount();
//        for (int i = 0; i < count; i++) {
//            if (children.get(i) == child) {
//                return i;
//            }
//        }
//        return -1;
//    }

//    @Implementation
//    public int getChildCount() {
//        return children.size();
//    }

//    @Implementation
//    public View getChildAt(int index) {
//        return children.get(index);
//    }

//    @Implementation
//    public void removeAllViews() {
//        for (int i = 0; i < realViewGroup.getChildCount(); i++) {
//            View child = realViewGroup.getChildAt(i);
//            shadowOf(child).parent = null;
//            if (onHierarchyChangeListener != null) {
//                onHierarchyChangeListener.onChildViewRemoved(this.realView, child);
//            }
//            removedChild(child);
//        }
//        children.clear();
//        requestLayout();
//    }

//    @Implementation
//    public void removeViewAt(int position) {
//        View child = children.remove(position);
//        shadowOf(child).parent = null;
//        removedChild(child);
//        requestLayout();
//    }
//
//    @Implementation
//    public void removeView(View child) {
//        // Android's ViewGroup ignores the child when it is null. Do the same here.
//        if (child == null) return;
//        if (children.remove(child)) {
//            shadowOf(child).parent = null;
//            removedChild(child);
//        }
//        requestLayout();
//    }

//    @Override
//    @Implementation
//    public boolean hasFocus() {
//        if (super.hasFocus()) return true;
//
//        for (int i = 0; i < realViewGroup.getChildCount(); i++) {
//            View child = realViewGroup.getChildAt(i);
//            if (child.hasFocus()) return true;
//        }
//
//        return false;
//    }
//
//    @Implementation
//    @Override
//    public void clearFocus() {
//        if (hasFocus()) {
//            super.clearFocus();
//
//            for (int i = 0; i < realViewGroup.getChildCount(); i++) {
//                View child = realViewGroup.getChildAt(i);
//                child.clearFocus();
//            }
//        }
//    }

//    @Implementation
//    public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener) {
//        this.onHierarchyChangeListener = onHierarchyChangeListener;
//    }

    /**
     * Returns a string representation of this {@code ViewGroup} by concatenating all of the strings contained in all
     * of the descendants of this {@code ViewGroup}.
     * <p/>
     * Robolectric extension.
     */
    @Override
    public String innerText() {
        String innerText = "";
        String delimiter = "";

        for (int i = 0; i < realViewGroup.getChildCount(); i++) {
            View child = realViewGroup.getChildAt(i);
            String childText = shadowOf(child).innerText();
            if (childText.length() > 0) {
                innerText += delimiter;
                delimiter = " ";
            }
            innerText += childText;
        }
        return innerText;
    }

    /**
     * Non-Android method that dumps the state of this {@code ViewGroup} to {@code System.out}
     */
    @Override
    public void dump(PrintStream out, int indent) {
        dumpFirstPart(out, indent);
        if (realViewGroup.getChildCount() > 0) {
            out.println(">");

            for (int i = 0; i < realViewGroup.getChildCount(); i++) {
                View child = realViewGroup.getChildAt(i);
                shadowOf(child).dump(out, indent + 2);
            }

            dumpIndent(out, indent);
            out.println("</" + realView.getClass().getSimpleName() + ">");
        } else {
            out.println("/>");
        }
    }

    @Implementation
    public void setLayoutAnimationListener(AnimationListener listener) {
        animListener = listener;
    }

    @Implementation
    public AnimationListener getLayoutAnimationListener() {
        return animListener;
    }

    @Implementation
    public void setLayoutAnimation(LayoutAnimationController layoutAnim) {
    	this.layoutAnim = layoutAnim;
    }
    
    @Implementation
    public LayoutAnimationController getLayoutAnimation() {
    	return layoutAnim;
    }

    @Implementation
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        disallowInterceptTouchEvent = disallowIntercept;
    }

    public boolean getDisallowInterceptTouchEvent() {
        return disallowInterceptTouchEvent;
    }

    protected void removedChild(View child) {
        if (isAttachedToWindow()) shadowOf(child).callOnDetachedFromWindow();
    }

    public MotionEvent getInterceptedTouchEvent() {
        return interceptedTouchEvent;
    }

    @Implementation
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        interceptedTouchEvent = ev;
        return false;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Implements(ViewGroup.LayoutParams.class)
    public static class ShadowLayoutParams {
        @RealObject private ViewGroup.LayoutParams realLayoutParams;

        public void __constructor__(int w, int h) {
            realLayoutParams.width = w;
            realLayoutParams.height = h;
        }

        public void __constructor__(ViewGroup.LayoutParams source) {
            __constructor__(source.width, source.height);
        }
    }

    /**
     * Shadow for {@link android.view.ViewGroup.MarginLayoutParams} that simulates its implementation.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Implements(ViewGroup.MarginLayoutParams.class)
    public static class ShadowMarginLayoutParams extends ShadowLayoutParams {

        @RealObject
        private ViewGroup.MarginLayoutParams realMarginLayoutParams;

        @Implementation
        public void setMargins(int left, int top, int right, int bottom) {
            realMarginLayoutParams.leftMargin = left;
            realMarginLayoutParams.topMargin = top;
            realMarginLayoutParams.rightMargin = right;
            realMarginLayoutParams.bottomMargin = bottom;
        }
    }
}
