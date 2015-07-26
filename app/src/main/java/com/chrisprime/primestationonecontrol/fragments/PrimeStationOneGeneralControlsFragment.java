package com.chrisprime.primestationonecontrol.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PrimeStationOneGeneralControlsFragment extends PrimeStationOneBaseSshCommanderFragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PrimeStationOneGeneralControlsFragment newInstance() {
        PrimeStationOneGeneralControlsFragment fragment = new PrimeStationOneGeneralControlsFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.btn_panic_kill_all_emus_and_es)
    Button mBtnPanicKillAllEmusEs;

    @Bind(R.id.btn_restart_primestation)
    Button mBtnRestartPrimestation;

    @Bind(R.id.btn_shutdown_primestation)
    Button mBtnShutdownPrimestation;

    @OnClick(R.id.btn_panic_kill_all_emus_and_es)
    void onPanicKillAllButtonClicked(View view) {
        Timber.d("Panic killAllEmusAndEs button clicked!");
        sendCommandToCurrentPrimeStationOne("killall emulationstation ; killall retroarch ; killall reicast ; emulationstation 2>&1 > /dev/tty1 &");
    }

    @OnClick(R.id.btn_restart_primestation)
    void onRestartPrimeStationButtonClicked(View view) {
        Timber.d("Restart Primestation button clicked!");
        sendCommandToCurrentPrimeStationOne("restart");
    }

    @OnClick(R.id.btn_shutdown_primestation)
    void onShutdownPrimeStationButtonClicked(View view) {
        Timber.d("Shutdown Primestation button clicked!");
        sendCommandToCurrentPrimeStationOne("off");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_primestation_one_general_controls, container, false);
        ButterKnife.bind(this, rootView);
        initializeButtonList();
        return rootView;
    }
}
