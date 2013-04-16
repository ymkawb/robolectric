package org.robolectric.shadows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.internal.RealObject;

import java.lang.ref.WeakReference;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(value = ViewStub.class, inheritImplementationMethods = true)
public class ShadowViewStub extends ShadowView {
    @RealObject ViewStub viewStub;

    private int mLayoutResource = 0;
    private int mInflatedId;
    private WeakReference<View> mInflatedViewRef;
    private ViewStub.OnInflateListener mInflateListener;

    @Override public void applyAttributes() {
        super.applyAttributes();

        mInflatedId = attributeSet.getAttributeResourceValue("android", "inflatedId", 0);
        mLayoutResource = attributeSet.getAttributeResourceValue("android", "layout", 0);
    }

    @Implementation
    public int getInflatedId() {
        return mInflatedId;
    }

    @Implementation
    public void setInflatedId(int inflatedId) {
        mInflatedId = inflatedId;
    }

    @Implementation
    public int getLayoutResource() {
        return mLayoutResource;
    }

    @Implementation
    public void setLayoutResource(int layoutResource) {
        mLayoutResource = layoutResource;
    }

    @Implementation
    public View inflate() {
        ViewParent viewParent = viewStub.getParent();

        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0) {
                ViewGroup parent = (ViewGroup) viewParent;
                LayoutInflater factory = LayoutInflater.from(viewStub.getContext());
                View view = factory.inflate(mLayoutResource, parent, false);

                if (mInflatedId != View.NO_ID) {
                    view.setId(mInflatedId);
                }

                int index = parent.indexOfChild(viewStub);
                parent.removeViewAt(index);
//        parent.removeViewInLayout(viewStub);

                ViewGroup.LayoutParams layoutParams = realView.getLayoutParams();
                if (layoutParams != null) {
                    parent.addView(view, index, layoutParams);
                } else {
                    parent.addView(view, index);
                }

                mInflatedViewRef = new WeakReference<View>(view);

                if (mInflateListener != null) {
                    mInflateListener.onInflate(viewStub, view);
                }

                return view;
            } else {
                throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
    }
}
