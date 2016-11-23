package com.chrisprime.primestationonecontrol.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.dagger.Injector
import com.chrisprime.primestationonecontrol.fragments.WebViewFragment

/**
 * Created by cpaian on 11/22/16.
 */
class PrimeStationOneVirtualGamepadActivity : BaseEventBusAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_gamepad)
        Injector.applicationComponent.inject(this)

        newMainFragment(WebViewFragment.newInstance(getString(R.string.title_primestation_virtual_gamepad), "http://" + getCurrentPrimeStationOne()!!.ipAddress + ":8080"))
    }

    fun newMainFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}