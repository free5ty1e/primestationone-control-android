package com.chrisprime.primestationonecontrol.fragments;

import android.app.Activity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;
import com.chrisprime.primestationonecontrol.utilities.TextViewUtilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class PrimeStationOneBaseSshCommanderFragment extends BaseFragment {

    protected Observable<Integer> mPrimeStationCommandObservable;
    protected Subscriber<Integer> mPrimeStationCommandSubscriber;
    protected Subscription mPrimeStationCommandSubscription;
    protected List<Button> mButtonList = new ArrayList<>();

    @Bind(R.id.sv_status)
    ScrollView mSvStatus;

    @Bind(R.id.tv_status)
    TextView mTvStatus;

    @Bind(R.id.ll_button_container)
    LinearLayout mButtonContainer;

    protected void sendCommandToCurrentPrimeStationOne(final String command, final boolean waitForReturnValueAndCommandOutput, final TextView textViewForConsoleUpdates) {
        PrimeStationOne currentPrimeStationOne = PrimeStationOneControlApplication.instance.getCurrentPrimeStationOne();
        if (currentPrimeStationOne == null) {
            Toast.makeText(getActivity(), "No Primestation currently selected, please select one from Search n Scan screen...", Toast.LENGTH_SHORT).show();
        } else {
            mTvStatus.setText("Sending command to current PrimeStation One at " + currentPrimeStationOne.getIpAddress() + ": " + command);
            setAllButtonsEnabledInList(false);
            mPrimeStationCommandObservable = Observable.create(
                    new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> sub) {
                            sub.onNext(NetworkUtilities.sendSshCommandToPi(currentPrimeStationOne.getIpAddress(),
                                    currentPrimeStationOne.getPiUser(),
                                    currentPrimeStationOne.getPiPassword(),
                                    PrimeStationOne.DEFAULT_PI_SSH_PORT, command, waitForReturnValueAndCommandOutput,
                                    line -> {
                                        String processedLine = processSshConsoleStdOutLine(line);
                                        Activity activity = getActivity();
                                        if (textViewForConsoleUpdates != null && activity != null) {
                                            activity.runOnUiThread(() -> TextViewUtilities.addLinesToTextView(processedLine,
                                                    textViewForConsoleUpdates, (ScrollView) textViewForConsoleUpdates.getParent()));
                                        }
                                    }));
                            sub.onCompleted();
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
                        TextViewUtilities.addLinesToTextView("\nCommand sent to current PrimeStation One at "
                                        + currentPrimeStationOne.getIpAddress(), PrimeStationOneBaseSshCommanderFragment.this.mTvStatus,
                                PrimeStationOneBaseSshCommanderFragment.this.mSvStatus);
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
    }

    /**
     * Override me to apply additional logic to any incoming SSH console stdout lines as they arrive, before they reach the terminal
     * @param line
     * @return
     */
    protected String processSshConsoleStdOutLine(String line) {
        return line;
    }

    protected boolean determineIsCommanderBusy() {
        return mPrimeStationCommandSubscription != null && !mPrimeStationCommandSubscriber.isUnsubscribed();
    }

    /**
     * Make sure to call this in your fragment's onCreateView!
     */
    protected void initializeCommander() {
        //Populate the button list so we can easily run through and enable or disable them
        for (int i = 0; i < mButtonContainer.getChildCount(); i++) {
            mButtonList.add((Button) mButtonContainer.getChildAt(i));
        }
    }

    protected void setAllButtonsEnabledInList(boolean enabled) {
        for (Button button : mButtonList) {
            button.setEnabled(enabled);
        }
    }
}
