package com.chrisprime.primestationonecontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrisprime.primestationonecontrol.R
import kotlinx.android.synthetic.main.fragment_primestation_one_general_controls.*
import timber.log.Timber

class PrimeStationOneGeneralControlsFragment : PrimeStationOneBaseSshCommanderFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_primestation_one_general_controls, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_panic_kill_all_emus_and_es.setOnClickListener { onPanicKillAllButtonClicked(it) }
        btn_restart_primestation.setOnClickListener { onRestartPrimeStationButtonClicked(it) }
        btn_shutdown_primestation.setOnClickListener { onShutdownPrimeStationButtonClicked(it) }
        initializeCommander(ll_button_container)
    }

    fun onPanicKillAllButtonClicked(view: View) {
        Timber.d("Panic killAllEmusAndEs button clicked!")
        sendCommandToCurrentPrimeStationOne("killall emulationstation ; killall retroarch ; killall reicast ; emulationstation 2>&1 > /dev/tty1 &", false, tv_status, tv_status, sv_status)
    }

    fun onRestartPrimeStationButtonClicked(view: View) {
        Timber.d("Restart Primestation button clicked!")
        sendCommandToCurrentPrimeStationOne("restart", false, tv_status, tv_status, sv_status)
    }

    fun onShutdownPrimeStationButtonClicked(view: View) {
        Timber.d("Shutdown Primestation button clicked!")
        sendCommandToCurrentPrimeStationOne("off", false, tv_status, tv_status, sv_status)
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): PrimeStationOneGeneralControlsFragment {
            val fragment = PrimeStationOneGeneralControlsFragment()
            val args = Bundle()
            //        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.arguments = args
            return fragment
        }
    }
}
