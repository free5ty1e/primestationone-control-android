package com.chrisprime.primestationonecontrol.utilities

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.AmbiguousViewMatcherException
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.util.HumanReadables
import android.support.test.espresso.web.sugar.Web
import android.support.test.espresso.web.webdriver.Locator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.chrisprime.primestationonecontrol.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

import rx.functions.Action0
import timber.log.Timber

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.espresso.web.sugar.Web.onWebView
import android.support.test.espresso.web.webdriver.DriverAtoms.findElement
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import com.google.common.collect.Iterables
import junit.framework.Assert.assertNotNull
import org.hamcrest.Matchers.equalToIgnoringCase

/**
 * Created by cpaian on 4/23/16.
 */
object TestUtilities {

    @JvmStatic val DEFAULT_MAX_WAIT_TIME_SECONDS = 60

    @JvmStatic var currentActivity: Activity? = null
        get() {
            waitForIdleSync()
            val activity = arrayOfNulls<Activity>(1)
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                try {
                    val activityCollection = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                    activity[0] = Iterables.getOnlyElement(activityCollection)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
            return activity[0]
        }

    @JvmStatic fun waitForIdleSync() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation?.waitForIdleSync()
    }

    @JvmStatic fun matchToolbarTitle(
            title: String): ViewInteraction {
        return onView(isAssignableFrom(Toolbar::class.java)).check(matches(withToolbarTitle(equalToIgnoringCase(title))))
    }

    @JvmStatic fun withToolbarTitle(textMatcher: Matcher<String>): Matcher<Any> {

        return object : BoundedMatcher<Any, Toolbar>(Toolbar::class.java) {
            public override fun matchesSafely(toolbar: Toolbar): Boolean {
                return textMatcher.matches(toolbar.title.toString())
            }

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                textMatcher.describeTo(description)
            }
        }
    }

    @JvmStatic val isTextSelected: Matcher<View>
        get() = object : TypeSafeMatcher<View>() {

            public override fun matchesSafely(view: View): Boolean {
                return view is TextView && view.isSelected()
            }

            override fun describeTo(description: Description) {
                description.appendText("is-selected=true")
            }
        }

    /**
     * Safely locates a styled tab for ViewInteraction,
     * ensuring we are only matching text on the visible textview class in the tabview,
     * as there could be both a TextView and an AppCompatTextView that have matching text.

     * @param context            Context we are working in
     * *
     * @param tabTitleResourceId String resource ID of the desired tab title to locate
     * *
     * @return a ViewInteraction object ready to perform clicks or matches against for simpler integration tests
     */
    @JvmStatic fun findTab(context: Context, tabTitleResourceId: Int): ViewInteraction {
        return onView(Matchers.allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), ViewMatchers.withText(context.resources.getString(tabTitleResourceId))))
    }

    @JvmStatic fun clickActionInOverflowMenuItem(menuItemResourceId: Int) {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext())
        onView(withId(menuItemResourceId)).perform(click())
    }

    @JvmStatic fun waitForControlToSatisfyViewMatcher(@IdRes resourceIdOfControlToCheck: Int, viewMatcher: Matcher<View>, maxWaitTimeSeconds: Int) {
        var controlSatisfiesViewMatcherCriteria = false
        var secondsWaitedThusFar = 0
        while (!controlSatisfiesViewMatcherCriteria) {
            try {
                findById(resourceIdOfControlToCheck).check(matches(isDisplayed())).check(matches(viewMatcher))
                controlSatisfiesViewMatcherCriteria = true
            } catch (e: Exception) {
                if (secondsWaitedThusFar++ < maxWaitTimeSeconds) {
                    Timber.w("Control currently does not satisfy the provided ViewMatcher,  waiting 1 of allowed %d seconds and trying again...", maxWaitTimeSeconds)
                    sleep(TimeManager.instance.ONE_SECOND_IN_MILLIS)
                } else {
                    Timber.e("Control never satisfied the provided ViewMatcher condition within the allowed %d seconds, failed!", maxWaitTimeSeconds)
                    break
                }
            }

        }
    }

    @JvmStatic fun actionToCheckIfTextIsVisible(@StringRes resourceId: Int): () -> Unit {
        return { findByText(resourceId).check(matches(isDisplayed())) }
    }

    @JvmStatic fun findByText(@StringRes resourceId: Int): ViewInteraction {
        return onView(withText(resourceId))
    }

    @JvmStatic fun actionToCheckIfIdIsVisible(@IdRes resourceId: Int): () -> Unit {
        return { findById(resourceId).check(matches(isDisplayed())) }
    }

    @JvmStatic fun findById(@IdRes resourceId: Int): ViewInteraction {
        return onView(withId(resourceId))
    }

    @JvmStatic fun actionToCheckIfWebViewElementIsVisible(locator: Locator, searchText: String): () -> Unit {
        return { assertNotNull(checkIfWebViewElementIsVisible(locator, searchText)) }
    }

    @JvmStatic fun checkIfWebViewElementIsVisible(locator: Locator, searchText: String): Web.WebInteraction<Void> {
        return onWebView().withElement(findElement(locator, searchText))
    }

    @JvmOverloads @JvmStatic fun waitFor(performCheck: () -> Unit, maxWaitTimeSeconds: Int = DEFAULT_MAX_WAIT_TIME_SECONDS) {
        var textVisibleYet = false
        var secondsWaitedThusFar = 0
        while (!textVisibleYet) {
            try {
                performCheck.invoke()
                textVisibleYet = true
            } catch (e: CustomFailureHandler.AssertionFailedWithCauseError) {
                if (secondsWaitedThusFar++ < maxWaitTimeSeconds) {
                    Timber.d("Check has not yet passed, waiting 1 of allowed %d seconds and trying again...", maxWaitTimeSeconds)
                    sleep(TimeManager.instance.ONE_SECOND_IN_MILLIS)
                } else {
                    Timber.e(e, "Check never passed within the allowed %d seconds, failed!", maxWaitTimeSeconds)
                    break
                }
            } catch (e: NoMatchingViewException) {
                if (secondsWaitedThusFar++ < maxWaitTimeSeconds) {
                    Timber.d("Check has not yet passed, waiting 1 of allowed %d seconds and trying again...", maxWaitTimeSeconds)
                    sleep(TimeManager.instance.ONE_SECOND_IN_MILLIS)
                } else {
                    Timber.e(e, "Check never passed within the allowed %d seconds, failed!", maxWaitTimeSeconds)
                    break
                }
            } catch (e: AmbiguousViewMatcherException) {
                if (secondsWaitedThusFar++ < maxWaitTimeSeconds) {
                    Timber.d("Check has not yet passed, waiting 1 of allowed %d seconds and trying again...", maxWaitTimeSeconds)
                    sleep(TimeManager.instance.ONE_SECOND_IN_MILLIS)
                } else {
                    Timber.e(e, "Check never passed within the allowed %d seconds, failed!", maxWaitTimeSeconds)
                    break
                }
            }

        }
    }

    @JvmStatic fun sleep(time: Long) {
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    @JvmStatic fun tapViewWithId(@IdRes viewId: Int) {
        findById(viewId).perform(click())
    }

    @JvmStatic fun tapViewWithText(@StringRes stringId: Int) {
        findByText(stringId).perform(click())
    }

    @JvmStatic fun isTextVisible(@StringRes searchText: Int): Boolean {
        var foundTheText = false
        var viewInteraction: ViewInteraction? = null
        try {
            viewInteraction = findByText(searchText).check(matches(isDisplayed()))
        } catch (e: CustomFailureHandler.AssertionFailedWithCauseError) {
            Timber.d(".isTextVisible() unable to find text, returning null ViewInteraction!")
        } catch (e: NoMatchingViewException) {
            Timber.d(".isTextVisible() unable to find text, returning null ViewInteraction!")
        } catch (e: AmbiguousViewMatcherException) {
            Timber.d(e, ".isTextVisible() found multiple views matching text, returning true!")
            foundTheText = true
        }

        if (viewInteraction != null) {
            foundTheText = true
        }
        return foundTheText
    }

    @JvmStatic fun isIdVisible(@IdRes resourceId: Int): Boolean {
        var foundTheView = false
        var viewInteraction: ViewInteraction? = null
        try {
            viewInteraction = findById(resourceId).check(matches(isDisplayed()))
        } catch (e: NoMatchingViewException) {
            Timber.d(".isIdVisible() unable to find id!")
        } catch (e: AmbiguousViewMatcherException) {
            Timber.d(e, ".isIdVisible() found multiple views matching id, returning true!")
            foundTheView = true
        }

        if (viewInteraction != null) {
            foundTheView = true
        }
        return foundTheView
    }

    @JvmOverloads @JvmStatic fun watchForText(@StringRes textToWaitFor: Int,
                                              doThisFirstEachCycle: Action0? = null, numSecondsToWaitBeforeFailure: Int = DEFAULT_MAX_WAIT_TIME_SECONDS) {
        val viewInteraction: ViewInteraction? = null
        for (i in 0..numSecondsToWaitBeforeFailure - 1) {
            doThisFirstEachCycle?.call()
            waitFor(actionToCheckIfTextIsVisible(textToWaitFor), 1)
            if (isTextVisible(textToWaitFor)) {
                Timber.d(".watchForText(): located requested text, exiting retry loop!")
                break
            }
        }
        assertNotNull(isTextVisible(textToWaitFor))
    }

    @JvmOverloads @JvmStatic fun watchForId(@IdRes idToWaitFor: Int,
                                            doThisFirstEachCycle: Action0? = null, numSecondsToWaitBeforeFailure: Int = DEFAULT_MAX_WAIT_TIME_SECONDS) {
        val viewInteraction: ViewInteraction? = null
        for (i in 0..numSecondsToWaitBeforeFailure - 1) {
            doThisFirstEachCycle?.call()
            waitFor(actionToCheckIfIdIsVisible(idToWaitFor), 1)
            if (isIdVisible(idToWaitFor)) {
                Timber.d(".watchForId(): located requested text, exiting retry loop!")
                break
            }
        }
        assertNotNull(isIdVisible(idToWaitFor))
    }

    @JvmStatic fun safeFindWebControl(searchText: String, locator: Locator): Web.WebInteraction<Void> {
        var webControl: Web.WebInteraction<Void>?
        try {
            webControl = checkIfWebViewElementIsVisible(locator, searchText)
        } catch (e: Exception) {
            webControl = null
        }

        return webControl!!
    }

    @JvmOverloads @JvmStatic fun watchForWebControl(searchText: String, locator: Locator,
                                         doThisFirstEachCycle: Action0? = null, numSecondsToWaitBeforeFailure: Int = DEFAULT_MAX_WAIT_TIME_SECONDS): Web.WebInteraction<Void> {
        var webControl: Web.WebInteraction<Void>? = safeFindWebControl(searchText, locator)
        if (webControl == null) {
            for (i in 0..numSecondsToWaitBeforeFailure - 1) {
                doThisFirstEachCycle?.call()
                waitFor(actionToCheckIfWebViewElementIsVisible(locator, searchText))
                webControl = safeFindWebControl(searchText, locator)
                if (webControl != null) {
                    Timber.d(".watchForWebControl(): located requested webcontrol, exiting retry loop!")
                    break
                }
            }
        }
        assertNotNull(webControl)
        return webControl!!
    }

    @JvmStatic fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {

        return RecyclerViewMatcher(recyclerViewId)
    }

    @JvmStatic fun getActivity(view: View): Activity {
        var context = view.context
        while (context !is Activity) {
            if (context is ContextWrapper) {
                context = context.baseContext
            } else {
                throw IllegalStateException("Got a context of class "
                        + context.javaClass
                        + " and I don't know how to get the Activity from it!")
            }
        }
        return context
    }

    private class ActionOnItemViewAtPositionViewAction<VH : RecyclerView.ViewHolder>(private val position: Int,
                                                                                     @IdRes private val viewId: Int,
                                                                                     private val viewAction: ViewAction) :

            ViewAction {

        override fun getConstraints(): Matcher<View> {
            //noinspection unchecked
            return Matchers.allOf(*arrayOf<Matcher<View>>(isAssignableFrom(RecyclerView::class.java), isDisplayed()))
        }

        override fun getDescription(): String {
            return "actionOnItemAtPosition performing ViewAction: " + this.viewAction.description + " on item at position: " + this.position
        }

        override fun perform(uiController: UiController, view: View) {
            val recyclerView = view as RecyclerView
            ScrollToPositionViewAction(this.position).perform(uiController, view)
            uiController.loopMainThreadUntilIdle()

            val targetView = recyclerView.getChildAt(this.position).findViewById(this.viewId)

            if (targetView == null) {
                throw PerformException.Builder().withActionDescription(this.toString()).withViewDescription(

                        HumanReadables.describe(view)).withCause(IllegalStateException(
                        "No view with id "
                                + this.viewId
                                + " found at position: "
                                + this.position)).build()
            } else {
                this.viewAction.perform(uiController, targetView)
            }
        }
    }

    private class ScrollToPositionViewAction(private val position: Int) : ViewAction {

        override fun getConstraints(): Matcher<View> {
            //noinspection unchecked
            return Matchers.allOf(*arrayOf<Matcher<View>>(isAssignableFrom(RecyclerView::class.java), isDisplayed()))
        }

        override fun getDescription(): String {
            return "scroll RecyclerView to position: " + this.position
        }

        override fun perform(uiController: UiController, view: View) {
            val recyclerView = view as RecyclerView
            recyclerView.scrollToPosition(this.position)
        }
    }

    @JvmStatic fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    @JvmStatic fun noDrawable(): Matcher<View> {
        return DrawableMatcher(-1)
    }


    @JvmStatic fun openNavDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
    }

    @JvmStatic fun closeNavDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close())
    }
}
