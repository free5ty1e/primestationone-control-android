package com.chrisprime.primestationonecontrol.bases;

import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity;

/**
 * Created by cpaian on 02/01/2016.
 */
abstract public class BaseNoUiAutomationTest extends BaseStatelessBlackBoxEspressoTest<PrimeStationOneControlActivity> {
    public BaseNoUiAutomationTest() {
        super(PrimeStationOneControlActivity.class);
    }
}
