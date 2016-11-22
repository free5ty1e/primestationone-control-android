package com.chrisprime.primestationonecontrol.views

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView

import com.chrisprime.primestationonecontrol.R

import butterknife.Bind
import butterknife.ButterKnife
import rx.functions.Action0

class DiscoveryEmptyView : ScrollView {

    @Bind(R.id.view_discovery_empty_logo)
    lateinit var mImageView: ImageView
    @Bind(R.id.view_discovery_empty_title_textview)
    lateinit var mTitleTextView: TextView
    @Bind(R.id.view_discovery_empty_body_textview)
    lateinit var mBodyTextView: TextView
    @Bind(R.id.view_discovery_empty_button)
    lateinit var mButton: Button

    internal var mAlignTop = false

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
        ButterKnife.bind(this)
    }

    fun setOnButtonClick(action0: () -> Unit) {
        mButton.setOnClickListener { v -> action0.invoke() }
    }

    fun setStrings(@StringRes titleStrId: Int, @StringRes bodyStrId: Int, @StringRes buttonStrId: Int) {
        if (titleStrId > -1) {
            mImageView.visibility = View.GONE
            mTitleTextView.visibility = View.VISIBLE
            mTitleTextView.setText(titleStrId)
        } else {
            mImageView.visibility = View.VISIBLE
            mTitleTextView.visibility = View.INVISIBLE
        }
        mBodyTextView.setText(bodyStrId)
        if (buttonStrId > -1) {
            mButton.visibility = View.VISIBLE
            mButton.setText(buttonStrId)
        } else {
            mButton.visibility = View.GONE
        }
    }

    companion object {

        val NULL_RESOURCE_ID = -1
    }
}
