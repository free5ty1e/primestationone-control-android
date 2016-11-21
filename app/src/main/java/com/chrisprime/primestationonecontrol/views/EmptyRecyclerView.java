package com.chrisprime.primestationonecontrol.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import rx.functions.Action0;

public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;
    private ProgressBar mProgressBar;

    private boolean mLoading = false;

    private Action0 mEmptyAction, mNonEmptyAction;

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
        if (mProgressBar != null) {
            mProgressBar.setVisibility(loading ? VISIBLE : GONE);
        }
        if (loading) {
            mEmptyView.setVisibility(GONE);
        } else {
            checkIfEmpty();
        }
    }

    public void checkIfEmpty() {
        if (getAdapter() == null) {
            return;
        }
        final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
        if (mEmptyView != null) {
            mEmptyView.setVisibility(emptyViewVisible && !mLoading ? VISIBLE : GONE);
            setVisibility(emptyViewVisible || mLoading ? GONE : VISIBLE);
        }

        if (emptyViewVisible && mEmptyAction != null) {
            mEmptyAction.call();
        } else if (!emptyViewVisible && mNonEmptyAction != null) {
            mNonEmptyAction.call();
        }
    }


    public void setEmptyAction(Action0 emptyAction) {
        mEmptyAction = emptyAction;
    }

    public void setNonEmptyAction(Action0 nonEmptyAction) {
        mNonEmptyAction = nonEmptyAction;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    public void setProgressView(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }
}
