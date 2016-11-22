package com.chrisprime.primestationonecontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.chrisprime.primestationonecontrol.R
import timber.log.Timber

class PrimeStationOneGeneralControlsFragment : PrimeStationOneBaseSshCommanderFragment() {

    @Bind(R.id.sv_status)
    lateinit var mSvStatus: ScrollView

    @Bind(R.id.tv_status)
    lateinit var mTvStatus: TextView

    @Bind(R.id.ll_button_container)
    lateinit var mButtonContainer: LinearLayout

    @Bind(R.id.btn_panic_kill_all_emus_and_es)
    lateinit var mBtnPanicKillAllEmusEs: Button

    @Bind(R.id.btn_restart_primestation)
    lateinit var mBtnRestartPrimestation: Button

    @Bind(R.id.btn_shutdown_primestation)
    lateinit var mBtnShutdownPrimestation: Button

    @OnClick(R.id.btn_panic_kill_all_emus_and_es)
    fun onPanicKillAllButtonClicked(view: View) {
        Timber.d("Panic killAllEmusAndEs button clicked!")
        sendCommandToCurrentPrimeStationOne("killall emulationstation ; killall retroarch ; killall reicast ; emulationstation 2>&1 > /dev/tty1 &", false, mTvStatus, mTvStatus, mSvStatus)
    }

    @OnClick(R.id.btn_restart_primestation)
    fun onRestartPrimeStationButtonClicked(view: View) {
        Timber.d("Restart Primestation button clicked!")
        sendCommandToCurrentPrimeStationOne("restart", false, mTvStatus, mTvStatus, mSvStatus)
    }

    @OnClick(R.id.btn_shutdown_primestation)
    fun onShutdownPrimeStationButtonClicked(view: View) {
        Timber.d("Shutdown Primestation button clicked!")
        sendCommandToCurrentPrimeStationOne("off", false, mTvStatus, mTvStatus, mSvStatus)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_primestation_one_general_controls, container, false)
        ButterKnife.bind(this, rootView)
        initializeCommander(mButtonContainer)
        return rootView
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
