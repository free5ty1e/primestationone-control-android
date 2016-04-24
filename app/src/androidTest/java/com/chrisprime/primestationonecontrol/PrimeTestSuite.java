package com.chrisprime.primestationonecontrol;

import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivityTest;
import com.chrisprime.primestationonecontrol.model.PrimeStationOneModelTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This test suite defines the order we want our tests to run in, allowing us to specify
 * to log in once, then test functionality requiring user to be logged in, then log out afterwards
 * instead of logging in and out on each test class's setUp() and tearDown() methods,
 * which is horribly inefficient.
 * <p>
 * Created by cpaian on 1/29/16.
 * <p>
 * IMPORTANT: THIS LIST OF TEST CLASSES MUST BE MAINTAINED MANUALLY WHENEVER
 * A TEST CLASS IS ADDED / REFACTORED / REMOVED, IN THE ORDER THE TESTS NEED TO
 * BE RUN!
 * <p>
 * TODO?: See http://stackoverflow.com/questions/28678026/how-can-i-get-all-class-files-in-a-specific-package-in-java for possibilty of only having to specify the package names instead of individual classes
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        //First run tests that do not require an activity to be actually up:
        ApplicationTest.class,
        PrimeStationOneModelTest.class,

        //Then run tests that exercise / involve the UI or require an activity to be actually up:
        PrimeStationOneControlActivityTest.class
})
public class PrimeTestSuite {
}
