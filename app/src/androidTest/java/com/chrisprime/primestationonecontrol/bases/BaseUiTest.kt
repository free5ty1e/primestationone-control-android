package com.chrisprime.primestationonecontrol.bases

import android.support.annotation.IdRes
import android.support.annotation.NonNull
import android.support.annotation.StringRes
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity
import com.chrisprime.primestationonecontrol.dagger.ApplicationComponentInstrumentationTest
import com.chrisprime.primestationonecontrol.dagger.InjectorInstrumentationTest
import com.chrisprime.primestationonecontrol.utilities.TestUtilities

/**
 * This should be the default base UI test, using the default entry point

 * Created by cpaian on 9/24/16.
 */

abstract class BaseUiTest @JvmOverloads protected constructor(clearUserDataBeforeLaunch: Boolean = false) : BaseInstrumentationTest<PrimeStationOneControlActivity>(PrimeStationOneControlActivity::class.java, clearUserDataBeforeLaunch) {

    override fun injectComponentsForInstrumentationTest() {   //Must perform injection one subclass down without a generic subtype
        InjectorInstrumentationTest.initializeApplicationComponent()
        (InjectorInstrumentationTest.applicationComponent as ApplicationComponentInstrumentationTest).inject(this)
    }

    fun navigateTo(@NonNull screenshotTag: String, @StringRes navItemTextResId: Int, @IdRes idToWatchFor: Int) {
        TestUtilities.openNavDrawer()
        TestUtilities.watchForText(navItemTextResId)
        screenshot("DrawerOpenFor$screenshotTag")
        TestUtilities.tapViewWithText(navItemTextResId)
        TestUtilities.watchForId(idToWatchFor)
        screenshot("${screenshotTag}View")
    }
}
