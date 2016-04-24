package com.chrisprime.primestationonecontrol.activities;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.bases.BaseUiAutomationTest;
import com.chrisprime.primestationonecontrol.utilities.TestUtilities;

import org.junit.Test;

/**
 * Created by cpaian on 4/23/16.
 */
public class PrimeStationOneControlActivityTest extends BaseUiAutomationTest {

    @Test
    public void testNavigationDrawer() throws Exception {
        screenshot("Start");
        TestUtilities.openNavDrawer();
        TestUtilities.watchForText(R.string.title_primestation_search);
        screenshot("DrawerOpen");
        TestUtilities.tapViewWithText(R.string.title_primestation_search);
        TestUtilities.watchForId(R.id.btn_find_pi);
        screenshot("SearchView");
    }
}