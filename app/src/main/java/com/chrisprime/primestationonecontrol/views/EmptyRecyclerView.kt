package com.chrisprime.primestationonecontrol.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

import rx.functions.Action0

class EmptyRecyclerView : RecyclerView {
    private var mEmptyView: View? = null
    private var mProgressBar: View? = null

    private var mLoading = false

    private var mEmptyAction: Action0? = null
    private var mNonEmptyAction: Action0? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    fun setLoading(loading: Boolean) {
        mLoading = loading
        if (mProgressBar != null) {
            mProgressBar!!.visibility = if (loading) View.VISIBLE else View.GONE
        }
        if (loading) {
            mEmptyView!!.visibility = View.GONE
        } else {
            checkIfEmpty()
        }
    }

    fun checkIfEmpty() {
        if (adapter == null) {
            return
        }
        val emptyViewVisible = adapter.itemCount == 0
        if (mEmptyView != null) {
            mEmptyView!!.visibility = if (emptyViewVisible && !mLoading) View.VISIBLE else View.GONE
            visibility = if (emptyViewVisible || mLoading) View.GONE else View.VISIBLE
        }

        if (emptyViewVisible && mEmptyAction != null) {
            mEmptyAction!!.call()
        } else if (!emptyViewVisible && mNonEmptyAction != null) {
            mNonEmptyAction!!.call()
        }
    }


    fun setEmptyAction(emptyAction: Action0) {
        mEmptyAction = emptyAction
    }

    fun setNonEmptyAction(nonEmptyAction: Action0) {
        mNonEmptyAction = nonEmptyAction
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }

    fun setEmptyView(emptyView: View) {
        mEmptyView = emptyView
        checkIfEmpty()
    }

    fun setProgressView(progressBar: View) {
        mProgressBar = progressBar
    }
}
