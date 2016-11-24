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

package com.chrisprime.primestationonecontrol.utilities

import android.content.Context
import android.support.test.espresso.EspressoException
import android.support.test.espresso.FailureHandler
import android.support.test.espresso.PerformException
import android.support.test.espresso.core.deps.guava.base.Preconditions
import android.support.test.espresso.core.deps.guava.base.Throwables
import android.view.View

import junit.framework.AssertionFailedError

import org.hamcrest.Matcher

/**
 * Recreating Espresso's [android.support.test.espresso.base.DefaultFailureHandler]
 * since the source class is final
 */
class CustomFailureHandler(
        appContext: Context) : FailureHandler {

    init {
        Preconditions.checkNotNull(appContext)
    }

    override fun handle(error: Throwable, viewMatcher: Matcher<View>) {
        if (error is EspressoException || error is AssertionFailedError
                || error is AssertionError || error is RuntimeException) {
            throw Throwables.propagate(getUserFriendlyError(error, viewMatcher))
        } else {
            throw Throwables.propagate(error)
        }
    }

    /**
     * When the error is coming from espresso, it is more user friendly to:
     * 1. propagate assertions as assertions
     * 2. swap the stack trace of the error to that of current thread (which will show
     * directly where the actual problem is)
     */
    private fun getUserFriendlyError(error: Throwable, viewMatcher: Matcher<View>): Throwable {
        var error = error
        if (error is PerformException) {
            // Re-throw the exception with the viewMatcher (used to locate the view) as the view
            // description (makes the error more readable). The reason we do this here: not all creators
            // of PerformException have access to the viewMatcher.
            throw PerformException.Builder()
                    .from(error)
                    .withViewDescription(viewMatcher.toString())
                    .build()
        }

        if (error is AssertionError) {
            // reports Failure instead of Error.
            // assertThat(...) throws an AssertionFailedError.
            error = AssertionFailedWithCauseError(error.message!!, error)
        }

//        if (error !is RuntimeException) { //Only set stack traces if not extending runtimeexception to keep our logs clean
//            error.setStackTrace(Thread.currentThread().stackTrace)
//        }
        return error
    }

    //Below needs to be public if we are going to catch this particular assertion and use it for retry logic...
    class AssertionFailedWithCauseError/* junit hides the cause constructor. */
    (message: String, cause: Throwable) : AssertionFailedError(message) {
//        init {
//            initCause(cause)
//        }
    }
}
