package com.chrisprime.primestationonecontrol.utilities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by cpaian on 2/17/16.
 */
public class DrawableMatcher extends TypeSafeMatcher<View> {

    private final int mExpectedId;
    private String mResourceName;

    public DrawableMatcher(int expectedId) {
        super(View.class);
        this.mExpectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)){
            return false;
        }
        ImageView imageView = (ImageView) target;
        if (mExpectedId < 0){
            return imageView.getDrawable() == null;
        }
        Drawable expectedDrawable = ContextCompat.getDrawable(target.getContext(), mExpectedId);
        if (expectedDrawable == null) {
            return false;
        }
        BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bmd.getBitmap();
        BitmapDrawable expected = (BitmapDrawable) expectedDrawable;
        Bitmap otherBitmap = expected.getBitmap();
        return bitmap.sameAs(otherBitmap);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(mExpectedId);
        if (mResourceName != null) {
            description.appendText("[");
            description.appendText(mResourceName);
            description.appendText("]");
        }
    }
}