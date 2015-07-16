package com.chrisprime.primestationonecontrol.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.utilities.SshUtilities;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PrimeStationOneDiscoveryFragment extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PrimeStationOneDiscoveryFragment newInstance() {
        PrimeStationOneDiscoveryFragment fragment = new PrimeStationOneDiscoveryFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //TODO: Delete this example once use @bind elsewhere
    @Bind(R.id.tv_found_pi)
    TextView mTvFoundPi;

    @OnClick(R.id.btn_find_pi)
    void onFindPiButtonClicked() {
        Timber.d("findPi button clicked!");

        Observable<String> findPiObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext(SshUtilities.findPi());
                        sub.onCompleted();
                    }
                }
        )
//                .map(s -> s + " -Love, Chris")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Subscriber<String> findPiSubscriber = new Subscriber<String>() {
            String foundPiVersion = null;
            @Override
            public void onNext(String s) {
                foundPiVersion = s;
                Timber.d("Found PrimestationOne version: " + s);
            }

            @Override
            public void onCompleted() {
                mTvFoundPi.setText(foundPiVersion);
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Error with subscriber: " + e.getMessage(), e);
            }
        };
        findPiObservable.subscribe(findPiSubscriber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
