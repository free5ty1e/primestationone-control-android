package com.chrisprime.primestationonecontrol.fragments;

import android.net.DhcpInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PrimeStationOneDiscoveryFragment extends Fragment {

    public static final String IP_SEPARATOR_CHAR_MATCHER = "\\.";
    public static final String IP_SEPARATOR_CHAR = ".";
    public static final int LAST_IP_OCTET_MAX = 255;

    List<String> mFoundPiVersions = new ArrayList<>();
    List<String> mFoundPiIps = new ArrayList<>();

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

    @Bind(R.id.tv_found_pi)
    TextView mTvFoundPi;

    @OnClick(R.id.btn_find_pi)
    void onFindPiButtonClicked(View view) {
        Timber.d("findPi button clicked!");
        Button findPiButton = (Button) view;
        findPiButton.setEnabled(false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final String gatewayPrefix = getCurrentGatewayPrefix();

        Observable<String> findPiObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext(checkForPi(gatewayPrefix, sub));
//                            sub.onCompleted();
                    }
                }
        )
//                .map(s -> s + " -Love, Chris")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Subscriber<String> findPiSubscriber = new Subscriber<String>() {


            @Override
            public void onNext(String s) {
                Timber.d("Found PrimestationOne version: " + s);
            }

            @Override
            public void onCompleted() {
                findPiButton.setEnabled(true);
                mTvFoundPi.setText(mFoundPiIps + "\n" + mFoundPiVersions);
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Error with subscriber: " + e.getMessage(), e);
            }
        };
        findPiObservable.subscribe(findPiSubscriber);

    }

    private String checkForPi(String gatewayPrefix, Subscriber<? super String> sub) {
        for (int ipLastOctetToTry = 1; ipLastOctetToTry <= LAST_IP_OCTET_MAX; ipLastOctetToTry++) {
            String ipAddressToTry = gatewayPrefix + ipLastOctetToTry;
            String primeStationVersion = NetworkUtilities.sshCheckForPi(ipAddressToTry);
            if (primeStationVersion.length() > 0) {
                mFoundPiVersions.add(primeStationVersion);
                mFoundPiIps.add(ipAddressToTry);
            }
        }
        sub.onCompleted();
        return "success";
    }

    @NonNull
    private String getCurrentGatewayPrefix() {
        DhcpInfo dhcpInfo = NetworkUtilities.getDhcpInfo(getActivity());
        StringBuffer stringBuffer = new StringBuffer();
        NetworkUtilities.putAddress(stringBuffer, dhcpInfo.gateway);
        String gatewayIp = stringBuffer.toString();
        String[] gatewayIpOctets = gatewayIp.split(IP_SEPARATOR_CHAR_MATCHER);

        String gatewayPrefix = gatewayIpOctets.length == 0 ? "" : gatewayIpOctets[0] + IP_SEPARATOR_CHAR + gatewayIpOctets[1] + IP_SEPARATOR_CHAR + gatewayIpOctets[2] + IP_SEPARATOR_CHAR;
        Timber.d("gatewayIpOctets = " + Arrays.toString(gatewayIpOctets) + ", gatewayPrefix = " + gatewayPrefix + ", gatewayIp = " + gatewayIp + ", DhcpInfo = " + dhcpInfo);
        return gatewayPrefix;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
