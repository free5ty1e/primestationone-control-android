package com.chrisprime.primestationonecontrol.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.AmbiguousViewMatcherException;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.web.sugar.Web;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import rx.functions.Action0;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalToIgnoringCase;

/**
 * Created by cpaian on 4/23/16.
 */
public class TestUtilities {

    public static final int DEFAULT_MAX_WAIT_TIME_SECONDS = 60;

    public static ViewInteraction matchToolbarTitle(
            String title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(equalToIgnoringCase(title))));
    }

    public static Matcher<Object> withToolbarTitle(final Matcher<String> textMatcher) {

        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle().toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<View> isTextSelected() {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return view instanceof TextView && (view).isSelected();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is-selected=true");
            }
        };
    }

    /**
     * Safely locates a styled tab for ViewInteraction,
     * ensuring we are only matching text on the visible textview class in the tabview,
     * as there could be both a TextView and an AppCompatTextView that have matching text.
     *
     * @param context            Context we are working in
     * @param tabTitleResourceId String resource ID of the desired tab title to locate
     * @return a ViewInteraction object ready to perform clicks or matches against for simpler integration tests
     */
    public static ViewInteraction findTab(Context context, int tabTitleResourceId) {
        return onView(Matchers.allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), ViewMatchers.withText(context.getResources().getString(tabTitleResourceId))));
    }

    public static void clickActionInOverflowMenuItem(int menuItemResourceId) {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withId(menuItemResourceId)).perform(click());
    }

    public static void waitForControlToSatisfyViewMatcher(@IdRes int resourceIdOfControlToCheck, Matcher<View> viewMatcher, int maxWaitTimeSeconds) {
        boolean controlSatisfiesViewMatcherCriteria = false;
        int secondsWaitedThusFar = 0;
        while (!controlSatisfiesViewMatcherCriteria) {
            try {
                findById(resourceIdOfControlToCheck).check(matches(isDisplayed())).check(matches(viewMatcher));
                controlSatisfiesViewMatcherCriteria = true;
            } catch (Exception e) {
                if (secondsWaitedThusFar++ < maxWaitTimeSeconds) {
                    Timber.w("Control currently does not satisfy the provided ViewMatcher,  waiting 1 of allowed %d seconds and trying again...", maxWaitTimeSeconds);
                    sleep(TimeManager.Companion.getInstance().getONE_SECOND_IN_MILLIS());
                } else {
                    Timber.e("Control never satisfied the provided ViewMatcher condition within the allowed %d seconds, failed!", maxWaitTimeSeconds);
                    break;
                }
            }
        }
    }

    public static Action0 actionToCheckIfTextIsVisible(@StringRes int resourceId) {
        return () -> findByText(resourceId).check(matches(isDisplayed()));
    }

    @NonNull
    public static ViewInteraction findByText(@StringRes int resourceId) {
        return onView(withText(resourceId));
    }

    public static Action0 actionToCheckIfIdIsVisible(@IdRes int resourceId) {
        return () -> findById(resourceId).check(matches(isDisplayed()));
    }

    @NonNull
    public static ViewInteraction findById(@IdRes int resourceId) {
        return onView(withId(resourceId));
    }

    public static Action0 actionToCheckIfWebViewElementIsVisible(Locator locator, String searchText) {
        return () -> assertNotNull(checkIfWebViewElementIsVisible(locator, searchText));
    }

    public static Web.WebInteraction<Void> checkIfWebViewElementIsVisible(Locator locator, String searchText) {
        return onWebView().withElement(findElement(locator, searchText));
    }

    public static void waitFor(@NonNull Action0 performCheck) {
        waitFor(performCheck, DEFAULT_MAX_WAIT_TIME_SECONDS);
    }

    public static void waitFor(@NonNull Action0 performCheck, int maxWaitTimeSeconds) {
        boolean textVisibleYet = false;
        int secondsWaitedThusFar = 0;
        while (!textVisibleYet) {
            try {
                performCheck.call();
                textVisibleYet = true;
            } catch (CustomFailureHandler.AssertionFailedWithCauseError | NoMatchingViewException | AmbiguousViewMatcherException e) {
                if (secondsWaitedThusFar++ < maxWaitTimeSeconds) {
                    Timber.d("Check has not yet passed, waiting 1 of allowed %d seconds and trying again...", maxWaitTimeSeconds);
                    sleep(TimeManager.Companion.getInstance().getONE_SECOND_IN_MILLIS());
                } else {
                    Timber.e(e, "Check never passed within the allowed %d seconds, failed!", maxWaitTimeSeconds);
                    break;
                }
            }
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void tapViewWithId(@IdRes int viewId) {
        findById(viewId).perform(click());
    }

    public static void tapViewWithText(@StringRes int stringId) {
        findByText(stringId).perform(click());
    }

    public static boolean isTextVisible(@StringRes int searchText) {
        boolean foundTheText = false;
        ViewInteraction viewInteraction = null;
        try {
            viewInteraction = findByText(searchText).check(matches(isDisplayed()));
        } catch (CustomFailureHandler.AssertionFailedWithCauseError | NoMatchingViewException e) {
            Timber.d(".isTextVisible() unable to find text, returning null ViewInteraction!");
        } catch (AmbiguousViewMatcherException e) {
            Timber.d(e, ".isTextVisible() found multiple views matching text, returning true!");
            foundTheText = true;
        }
        if (viewInteraction != null) {
            foundTheText = true;
        }
        return foundTheText;
    }

    public static boolean isIdVisible(@IdRes int resourceId) {
        boolean foundTheView = false;
        ViewInteraction viewInteraction = null;
        try {
            viewInteraction = findById(resourceId).check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            Timber.d(".isIdVisible() unable to find id!");
        } catch (AmbiguousViewMatcherException e) {
            Timber.d(e, ".isIdVisible() found multiple views matching id, returning true!");
            foundTheView = true;
        }
        if (viewInteraction != null) {
            foundTheView = true;
        }
        return foundTheView;
    }

    public static void watchForText(@StringRes int textToWaitFor) {
        watchForText(textToWaitFor, null, DEFAULT_MAX_WAIT_TIME_SECONDS);
    }

    public static void watchForText(@StringRes int textToWaitFor,
                                    Action0 doThisFirstEachCycle) {
        watchForText(textToWaitFor, doThisFirstEachCycle, DEFAULT_MAX_WAIT_TIME_SECONDS);
    }

    public static void watchForText(@StringRes int textToWaitFor,
                                    Action0 doThisFirstEachCycle, int numSecondsToWaitBeforeFailure) {
        ViewInteraction viewInteraction = null;
        for (int i = 0; i < numSecondsToWaitBeforeFailure; i++) {
            if (doThisFirstEachCycle != null) {
                doThisFirstEachCycle.call();
            }
            waitFor(actionToCheckIfTextIsVisible(textToWaitFor), 1);
            if (isTextVisible(textToWaitFor)) {
                Timber.d(".watchForText(): located requested text, exiting retry loop!");
                break;
            }
        }
        assertNotNull(isTextVisible(textToWaitFor));
    }

    public static void watchForId(@IdRes int idToWaitFor) {
        watchForId(idToWaitFor, null);
    }

    public static void watchForId(@IdRes int idToWaitFor,
                                  Action0 doThisFirstEachCycle) {
        watchForId(idToWaitFor, doThisFirstEachCycle, DEFAULT_MAX_WAIT_TIME_SECONDS);
    }

    public static void watchForId(@IdRes int idToWaitFor,
                                  Action0 doThisFirstEachCycle, int numSecondsToWaitBeforeFailure) {
        ViewInteraction viewInteraction = null;
        for (int i = 0; i < numSecondsToWaitBeforeFailure; i++) {
            if (doThisFirstEachCycle != null) {
                doThisFirstEachCycle.call();
            }
            waitFor(actionToCheckIfIdIsVisible(idToWaitFor), 1);
            if (isIdVisible(idToWaitFor)) {
                Timber.d(".watchForId(): located requested text, exiting retry loop!");
                break;
            }
        }
        assertNotNull(isIdVisible(idToWaitFor));
    }

    public static Web.WebInteraction<Void> safeFindWebControl(String searchText, Locator locator) {
        Web.WebInteraction<Void> webControl;
        try {
            webControl = checkIfWebViewElementIsVisible(locator, searchText);
        } catch (Exception e) {
            webControl = null;
        }
        return webControl;
    }

    public static Web.WebInteraction<Void> watchForWebControl(String searchText, Locator locator) {
        return watchForWebControl(searchText, locator, null, DEFAULT_MAX_WAIT_TIME_SECONDS);
    }

    public static Web.WebInteraction<Void> watchForWebControl(String searchText, Locator locator,
                                                              Action0 doThisFirstEachCycle, int numSecondsToWaitBeforeFailure) {
        Web.WebInteraction<Void> webControl = safeFindWebControl(searchText, locator);
        if (webControl == null) {
            for (int i = 0; i < numSecondsToWaitBeforeFailure; i++) {
                if (doThisFirstEachCycle != null) {
                    doThisFirstEachCycle.call();
                }
                waitFor(actionToCheckIfWebViewElementIsVisible(locator, searchText));
                webControl = safeFindWebControl(searchText, locator);
                if (webControl != null) {
                    Timber.d(".watchForWebControl(): located requested webcontrol, exiting retry loop!");
                    break;
                }
            }
        }
        assertNotNull(webControl);
        return webControl;
    }

    public static <VH extends RecyclerView.ViewHolder> ViewAction actionOnItemViewAtPosition(int position,
                                                                                             @IdRes
                                                                                             int viewId,
                                                                                             ViewAction viewAction) {
        return new ActionOnItemViewAtPositionViewAction(position, viewId, viewAction);
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {

        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static Activity getActivity(View view) {
        Context context = view.getContext();
        while (!(context instanceof Activity)) {
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                throw new IllegalStateException("Got a context of class "
                        + context.getClass()
                        + " and I don't know how to get the Activity from it!");
            }
        }
        return (Activity) context;
    }

    private static final class ActionOnItemViewAtPositionViewAction<VH extends RecyclerView
            .ViewHolder>
            implements

            ViewAction {
        private final int position;
        private final ViewAction viewAction;
        private final int viewId;

        private ActionOnItemViewAtPositionViewAction(int position,
                                                     @IdRes int viewId,
                                                     ViewAction viewAction) {
            this.position = position;
            this.viewAction = viewAction;
            this.viewId = viewId;
        }

        public Matcher<View> getConstraints() {
            //noinspection unchecked
            return Matchers.allOf(new Matcher[]{
                    isAssignableFrom(RecyclerView.class), isDisplayed()
            });
        }

        public String getDescription() {
            return "actionOnItemAtPosition performing ViewAction: "
                    + this.viewAction.getDescription()
                    + " on item at position: "
                    + this.position;
        }

        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            (new ScrollToPositionViewAction(this.position)).perform(uiController, view);
            uiController.loopMainThreadUntilIdle();

            View targetView = recyclerView.getChildAt(this.position).findViewById(this.viewId);

            if (targetView == null) {
                throw (new PerformException.Builder()).withActionDescription(this.toString())
                        .withViewDescription(

                                HumanReadables.describe(view))
                        .withCause(new IllegalStateException(
                                "No view with id "
                                        + this.viewId
                                        + " found at position: "
                                        + this.position))
                        .build();
            } else {
                this.viewAction.perform(uiController, targetView);
            }
        }
    }

    private static final class ScrollToPositionViewAction implements ViewAction {
        private final int position;

        private ScrollToPositionViewAction(int position) {
            this.position = position;
        }

        public Matcher<View> getConstraints() {
            //noinspection unchecked
            return Matchers.allOf(new Matcher[]{
                    isAssignableFrom(RecyclerView.class), isDisplayed()
            });
        }

        public String getDescription() {
            return "scroll RecyclerView to position: " + this.position;
        }

        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.scrollToPosition(this.position);
        }
    }

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }


    public static void openNavDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    }

    public static void closeNavDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
    }
}
