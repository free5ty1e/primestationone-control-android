package com.chrisprime.primestationonecontrol.bases;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.runner.RunWith;

/**
 * Created by cpaian on 7/16/15.
 */
@RunWith(AndroidJUnit4.class)
abstract public class BaseStatelessBlackBoxEspressoTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    protected Activity mActivity;

    public BaseStatelessBlackBoxEspressoTest(Class<T> clazz) {
        super(clazz);
    }

    protected Activity getCurrentActivity() {
        waitForIdleSync();
        final Activity[] activity = new Activity[1];
        try {
            runTestOnUiThread(() -> {
                java.util.Collection<Activity> activites = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                activity[0] = Iterables.getOnlyElement(activites);
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return activity[0];
    }

    protected void waitForIdleSync() {
        Instrumentation instrumentation = getInstrumentation();
        if (instrumentation != null) {
            instrumentation.waitForIdleSync();
        }
    }
}
