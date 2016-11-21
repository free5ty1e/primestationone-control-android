package com.chrisprime.primestationonecontrol.activities

import android.support.v7.app.AppCompatActivity

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.squareup.otto.Bus

/**
 * Created by cpaian on 7/26/15.
 */
open class BaseEventBusAppCompatActivity : AppCompatActivity() {
    protected var mEventBus = PrimeStationOneControlApplication.eventBus

    public override fun onResume() {
        super.onResume()
        mEventBus.register(this)
    }

    public override fun onPause() {
        super.onPause()
        mEventBus.unregister(this)
    }
}

