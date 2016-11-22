package com.chrisprime.primestationonecontrol.views

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import com.chrisprime.primestationonecontrol.R
import kotlinx.android.synthetic.main.view_discovery_empty.view.*

class DiscoveryEmptyView : ScrollView {

    constructor(context: Context) : super(context) {
        if (!isInEditMode) {
            init(null)
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        if (!isInEditMode) {
            init(attrs)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (!isInEditMode) {
            init(attrs)
        }
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_discovery_empty, this)
        isFillViewport = true
    }

    fun setOnButtonClick(action0: () -> Unit) {
        view_discovery_empty_button.setOnClickListener { v -> action0.invoke() }
    }

    fun setStrings(@StringRes titleStrId: Int, @StringRes bodyStrId: Int, @StringRes buttonStrId: Int) {
        if (titleStrId > -1) {
            view_discovery_empty_logo.visibility = View.GONE
            view_discovery_empty_title_textview.visibility = View.VISIBLE
            view_discovery_empty_title_textview.setText(titleStrId)
        } else {
            view_discovery_empty_logo.visibility = View.VISIBLE
            view_discovery_empty_title_textview.visibility = View.INVISIBLE
        }
        view_discovery_empty_body_textview.setText(bodyStrId)
        if (buttonStrId > -1) {
            view_discovery_empty_button.visibility = View.VISIBLE
            view_discovery_empty_button.setText(buttonStrId)
        } else {
            view_discovery_empty_button.visibility = View.GONE
        }
    }
}
