package com.chrisprime.primestationonecontrol.testutilities;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by cpaian on 7/16/15.
 */
public abstract class BaseStatelessBlackBoxEspressoTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    private SystemAnimations mSystemAnimations;

    public BaseStatelessBlackBoxEspressoTest(Class clazz) {
        super(clazz);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSystemAnimations = new SystemAnimations(getInstrumentation().getContext());
        mSystemAnimations.disableAll();
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // If you want to test this entire Gist is working, comment out this line and
        // run a test.  Then check these options in Settings -> Developer:
        //  -Window animation scale
        //  -Transition animation scale
        //  -Animator duration scale
        // They should all show "Animation off".  Then re-enable this line and run a
        // test again, this time it will show "Animation scale 1x"
        mSystemAnimations.enableAll();
    }
}