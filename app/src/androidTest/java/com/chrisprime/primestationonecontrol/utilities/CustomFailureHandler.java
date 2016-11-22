/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisprime.primestationonecontrol.utilities;

import android.content.Context;
import android.support.test.espresso.EspressoException;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.support.test.espresso.core.deps.guava.base.Throwables;
import android.view.View;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;

//TODO: Figure out how to propertly convert this to Kotlin without breaking this or losing functionality for retry logic
/**
 * Recreating Espresso's {@link android.support.test.espresso.base.DefaultFailureHandler}
 * since the source class is final
 */
public final class CustomFailureHandler implements FailureHandler {

    public CustomFailureHandler(
            Context appContext) {
        Preconditions.checkNotNull(appContext);
    }

    @Override
    public void handle(Throwable error, Matcher<View> viewMatcher) {
        if (error instanceof EspressoException || error instanceof AssertionFailedError
                || error instanceof AssertionError || error instanceof RuntimeException) {
            throw Throwables.propagate(getUserFriendlyError(error, viewMatcher));
        } else {
            throw Throwables.propagate(error);
        }
    }

    /**
     * When the error is coming from espresso, it is more user friendly to:
     * 1. propagate assertions as assertions
     * 2. swap the stack trace of the error to that of current thread (which will show
     * directly where the actual problem is)
     */
    private Throwable getUserFriendlyError(Throwable error, Matcher<View> viewMatcher) {
        if (error instanceof PerformException) {
            // Re-throw the exception with the viewMatcher (used to locate the view) as the view
            // description (makes the error more readable). The reason we do this here: not all creators
            // of PerformException have access to the viewMatcher.
            throw new PerformException.Builder()
                    .from((PerformException) error)
                    .withViewDescription(viewMatcher.toString())
                    .build();
        }

        if (error instanceof AssertionError) {
            // reports Failure instead of Error.
            // assertThat(...) throws an AssertionFailedError.
            error = new AssertionFailedWithCauseError(error.getMessage(), error);
        }

        if (!(error instanceof RuntimeException)) { //Only set stack traces if not extending runtimeexception to keep our logs clean
            error.setStackTrace(Thread.currentThread().getStackTrace());
        }
        return error;
    }

    //Below needs to be public if we are going to catch this particular assertion and use it for retry logic...
    static final class AssertionFailedWithCauseError extends AssertionFailedError {
        /* junit hides the cause constructor. */
        AssertionFailedWithCauseError(String message, Throwable cause) {
            super(message);
            initCause(cause);
        }
    }
}
