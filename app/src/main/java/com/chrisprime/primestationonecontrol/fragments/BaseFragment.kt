package com.chrisprime.primestationonecontrol.fragments

import android.support.v4.app.Fragment
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity
import com.chrisprime.primestationonecontrol.utilities.PreferenceStore
import javax.inject.Inject

/**
 * Created by cpaian on 7/26/15.
 */
open class BaseFragment : Fragment() {
    protected var mEventBus = PrimeStationOneControlApplication.eventBus

    @Inject
    lateinit var mPreferenceStore: PreferenceStore

    override fun onResume() {
        super.onResume()
        mEventBus.register(this)
    }

    /**
     * Runs the Runnable on the UI thread and discard the runnable if the activity has been nulled out.

     * @param runnable a Runnable to run if the activity isn't null
     */
    protected fun safeRunOnUiThread(runnable: Runnable) {
        val activity = activity
        activity?.runOnUiThread(runnable)
    }

    /**
     * Runs the Runnable on the I/O thread and discard the runnable if the activity has been nulled out.

     * @param runnable a Runnable to run if the activity isn't null
     */
    protected fun safeRunOnIoThread(runnable: Runnable) {
        val activity = activity as PrimeStationOneControlActivity
        activity.runOnIoThread(runnable)
    }

    override fun onPause() {
        super.onPause()
        mEventBus.unregister(this)
    }
}
