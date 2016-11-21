package com.chrisprime.primestationonecontrol.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action0;

public class DiscoveryEmptyView extends ScrollView {

    public static final int NULL_RESOURCE_ID = -1;

    @Bind(R.id.view_discovery_empty_logo)
    ImageView mImageView;
    @Bind(R.id.view_discovery_empty_title_textview)
    TextView mTitleTextView;
    @Bind(R.id.view_discovery_empty_body_textview)
    TextView mBodyTextView;
    @Bind(R.id.view_discovery_empty_button)
    Button mButton;

    boolean mAlignTop = false;

    public DiscoveryEmptyView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(null);
        }
    }

    public DiscoveryEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public DiscoveryEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    private void init(@Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.view_discovery_empty, this);
        setFillViewport(true);
        ButterKnife.bind(this);
    }

    public void setOnButtonClick(@NonNull Action0 action0) {
        mButton.setOnClickListener(v -> action0.call());
    }

    public void setStrings(@StringRes int titleStrId, @StringRes int bodyStrId, @StringRes int buttonStrId) {
        if (titleStrId > -1) {
            mImageView.setVisibility(View.GONE);
            mTitleTextView.setVisibility(VISIBLE);
            mTitleTextView.setText(titleStrId);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mTitleTextView.setVisibility(INVISIBLE);
        }
        mBodyTextView.setText(bodyStrId);
        if (buttonStrId > -1) {
            mButton.setVisibility(VISIBLE);
            mButton.setText(buttonStrId);
        } else {
            mButton.setVisibility(GONE);
        }
    }
}
