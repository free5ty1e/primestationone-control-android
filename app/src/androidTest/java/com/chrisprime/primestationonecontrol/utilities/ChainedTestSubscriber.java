package com.chrisprime.primestationonecontrol.utilities;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;
import timber.log.Timber;

/**
 * Allows you to chain multiple network calls and await on the last item in the chain to finish
 * before continuing in the unit test. There can also be some asserts on the complete and on next.
 */
public class ChainedTestSubscriber<T> extends TestSubscriber<T> {
    final CountDownLatch doneLatch = new CountDownLatch(1);

    @Override
    public void onCompleted() {
        super.onCompleted();
        try {
            doOnCompletedAsserts();
        } finally {
            doneLatch.countDown();
        }
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        Timber.e(e, "There was a PROBLEM: " + e.getLocalizedMessage());
        doneLatch.countDown();
    }

    @Override
    public void onNext(T obj) {
        super.onNext(obj);
        Timber.d("%s", obj);

        doOnNextAsserts(obj);

        doOnNextChain(obj);
    }

    public void doOnCompletedAsserts() {

    }

    public void doOnNextAsserts(T obj) {
        Assert.assertNotNull(obj);
    }

    public void doOnNextChain(T obj) {
        //do nothing.
    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        //we want to wait until both service calls are complete before proceeding.
        doneLatch.await(timeout, unit);
    }

}
