package com.chrisprime.primestationonecontrol.activities

import android.support.test.espresso.Espresso
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.bases.BaseUiAutomationTest
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import org.junit.Test

/**
 * Created by cpaian on 4/23/16.
 */
class PrimeStationOneControlActivityTest : BaseUiAutomationTest() {

    @Test
    @Throws(Exception::class)
    fun testNavigationDrawer() {
        screenshot("Start")
        navigateTo("Discovery", R.string.title_primestation_search, R.id.btn_find_pi)
        navigateTo("General", R.string.title_primestation_general_controls, R.id.btn_panic_kill_all_emus_and_es)
        navigateTo("CloudBak", R.string.title_primestation_cloud_backup_controls, R.id.button_login_to_mega)
        PrimeStationOneControlApplication.instance.currentPrimeStationOne = PrimeStationOne.generatePrimeStationOne()

        //Commented out for Travis testing:
//        navigateTo("VirtualGamePad", R.string.title_primestation_virtual_gamepad, R.id.fragment_webview_webview)
//        Espresso.pressBack()

        navigateTo("Settings", R.string.title_activity_settings, android.R.id.list)
        Espresso.pressBack()
    }
}
