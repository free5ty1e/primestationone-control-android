package com.chrisprime.primestationonecontrol.utilities

import junit.framework.Assert
import rx.observers.TestSubscriber
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Allows you to chain multiple network calls and await on the last item in the chain to finish
 * before continuing in the unit test. There can also be some asserts on the complete and on next.
 */
class ChainedTestSubscriber<T> : TestSubscriber<T>() {
    internal val doneLatch = CountDownLatch(1)

    override fun onCompleted() {
        super.onCompleted()
        try {
            doOnCompletedAsserts()
        } finally {
            doneLatch.countDown()
        }
    }

    override fun onError(e: Throwable) {
        super.onError(e)
        Timber.e(e, "There was a PROBLEM: " + e.message)
        doneLatch.countDown()
    }

    override fun onNext(obj: T) {
        super.onNext(obj)
        Timber.d("%s", obj)

        doOnNextAsserts(obj)

        doOnNextChain(obj)
    }

    fun doOnCompletedAsserts() {

    }

    fun doOnNextAsserts(obj: T) {
        Assert.assertNotNull(obj)
    }

    fun doOnNextChain(obj: T) {
        //do nothing.
    }

    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit) {
        //we want to wait until both service calls are complete before proceeding.
        doneLatch.await(timeout, unit)
    }

}
