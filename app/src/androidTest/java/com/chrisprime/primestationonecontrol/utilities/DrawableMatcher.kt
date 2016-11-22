package com.chrisprime.primestationonecontrol.utilities

import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * Created by cpaian on 2/17/16.
 */
class DrawableMatcher(private val mExpectedId: Int) : TypeSafeMatcher<View>(View::class.java) {
    private val mResourceName: String? = null

    override fun matchesSafely(target: View): Boolean {
        if (target !is ImageView) {
            return false
        }
        if (mExpectedId < 0) {
            return target.drawable == null
        }
        val expectedDrawable = ContextCompat.getDrawable(target.getContext(), mExpectedId) ?: return false
        val bmd = target.drawable as BitmapDrawable
        val bitmap = bmd.bitmap
        val expected = expectedDrawable as BitmapDrawable
        val otherBitmap = expected.bitmap
        return bitmap.sameAs(otherBitmap)
    }

    override fun describeTo(description: Description) {
        description.appendText("with drawable from resource id: ")
        description.appendValue(mExpectedId)
        if (mResourceName != null) {
            description.appendText("[")
            description.appendText(mResourceName)
            description.appendText("]")
        }
    }
}