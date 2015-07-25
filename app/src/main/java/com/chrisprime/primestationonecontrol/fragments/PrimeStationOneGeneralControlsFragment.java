package com.chrisprime.primestationonecontrol.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class PrimeStationOneGeneralControlsFragment extends Fragment {

    private Observable<Integer> mPrimeStationCommandObservable;
    private Subscriber<Integer> mPrimeStationCommandSubscriber;
    private Subscription mPrimeStationCommandSubscription;
    private List<Button> mButtonList = new ArrayList<>();

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

    @Bind(R.id.tv_status)
    TextView mTvStatus;

    @Bind(R.id.btn_panic_kill_all_emus_and_es)
    Button mBtnPanicKillAllEmusEs;

    @Bind(R.id.btn_restart_primestation)
    Button mBtnRestartPrimestation;

    @Bind(R.id.btn_shutdown_primestation)
    Button mBtnShutdownPrimestation;

    @Bind(R.id.ll_button_container)
    LinearLayout mButtonContainer;

    @OnClick(R.id.btn_panic_kill_all_emus_and_es)
    void onPanicKillAllButtonClicked(View view) {
        Timber.d("Panic killAllEmusAndEs button clicked!");
        setAllButtonsEnabledInList(false);

        mPrimeStationCommandObservable = Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> sub) {
                        PrimeStationOne currentPrimeStationOne = PrimeStationOneControlApplication.getInstance().getCurrentPrimeStationOne();
                        String command = "killall retroarch && killall emulationstation && emulationstation";
                        sub.onNext(NetworkUtilities.sendSshCommandToPi(currentPrimeStationOne.getIpAddress(), PrimeStationOne.DEFAULT_PI_USERNAME,
                                PrimeStationOne.DEFAULT_PI_PASSWORD, PrimeStationOne.DEFAULT_PI_SSH_PORT, command));
                    }
                }
        )
//                .map(s -> s + " -Love, Chris")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mPrimeStationCommandSubscriber = new Subscriber<Integer>() {

            @Override
            public void onNext(Integer i) {
                Timber.d(".onNext(command exit code: " + i + ")");
            }

            @Override
            public void onCompleted() {
                //                findPiButton.setEnabled(true);
                getActivity().runOnUiThread(() -> {
                    mTvStatus.setText("KillAll completed!");
                    setAllButtonsEnabledInList(true);
                });
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error with subscriber: " + e + ": " + e.getMessage());
            }
        };
        mPrimeStationCommandSubscription = mPrimeStationCommandObservable.subscribe(mPrimeStationCommandSubscriber);
    }

    private boolean determineIsBusy() {
        return mPrimeStationCommandSubscription != null && !mPrimeStationCommandSubscriber.isUnsubscribed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_primestation_one_general_controls, container, false);
        ButterKnife.bind(this, rootView);
        initializeButtonList();
        return rootView;
    }

    private void initializeButtonList() {
        //Populate the button list so we can easily run through and enable or disable them
        for (int i = 0; i < mButtonContainer.getChildCount(); i++) {
            mButtonList.add((Button) mButtonContainer.getChildAt(i));
        }
    }

    private void setAllButtonsEnabledInList(boolean enabled) {
        for (Button button : mButtonList) {
            button.setEnabled(enabled);
        }
    }
}
