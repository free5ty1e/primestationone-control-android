package com.chrisprime.primestationonecontrol.fragments;

import android.net.DhcpInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;
import com.chrisprime.primestationonecontrol.views.FoundPrimestationsRecyclerViewAdapter;

import java.net.InetAddress;
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

    private List<PrimeStationOne> mPrimeStationOneList = new ArrayList<>();
    private FoundPrimestationsRecyclerViewAdapter mFoundPrimestationsRecyclerViewAdapter;

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

    @Bind(R.id.rv_pi_list)
    RecyclerView mRvPiList;

    @OnClick(R.id.btn_find_pi)
    void onFindPiButtonClicked(View view) {
        Timber.d("findPi button clicked!");
        mPrimeStationOneList.clear();
        Button findPiButton = (Button) view;
        findPiButton.setEnabled(false);

        //TODO: Just put in a wifi wakelock, but for now this lazy thing works
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final String gatewayPrefix = getCurrentGatewayPrefix();

        Observable<String> findPiObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext(checkForPi(gatewayPrefix, sub));
                    }
                }
        )
//                .map(s -> s + " -Love, Chris")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Subscriber<String> findPiSubscriber = new Subscriber<String>() {

            @Override
            public void onNext(String s) {
                Timber.d(".onNext(" + s + ")");
            }

            @Override
            public void onCompleted() {
                findPiButton.setEnabled(true);
                int numPrimestationsFound = mPrimeStationOneList.size();
                mTvFoundPi.setText(numPrimestationsFound > 0 ?
                        numPrimestationsFound > 1 ? "Found " + numPrimestationsFound + " Primestations! xD" : "Found Primestation! :D"
                        : "None found :(");

                mFoundPrimestationsRecyclerViewAdapter = new FoundPrimestationsRecyclerViewAdapter(getActivity(), mPrimeStationOneList);
                mRvPiList.setAdapter(mFoundPrimestationsRecyclerViewAdapter);
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e("Error with subscriber: " + e + ": " + e.getMessage(), e);
            }
        };
        findPiObservable.subscribe(findPiSubscriber);

    }

    private String getHostname(String ipAddress, Subscriber<? super String> subscriber) {
        String hostname = "hostname";
        InetAddress address;
        try {
            address = InetAddress.getByName(ipAddress);
            Timber.d("InetAddress for " + ipAddress + " = " + address);
            hostname = address.getCanonicalHostName();
            Timber.d("IP " + ipAddress + " hostname = " + hostname);
        } catch (Exception e) {
            Timber.e("error obtaining hostname from " + ipAddress + ": " + e);
            subscriber.onError(e);
        }
        return hostname;
    }

    private String checkForPi(String gatewayPrefix, Subscriber<? super String> sub) {
        for (int ipLastOctetToTry = NetworkUtilities.LAST_IP_OCTET_MIN;
             ipLastOctetToTry <= NetworkUtilities.LAST_IP_OCTET_MAX; ipLastOctetToTry++) {
            String ipAddressToTry = gatewayPrefix + ipLastOctetToTry;
            String primeStationVersion = NetworkUtilities.sshCheckForPi(ipAddressToTry);
            if (primeStationVersion.length() > 0) {
                String hostname = getHostname(ipAddressToTry, sub);
                PrimeStationOne primeStationOne = new PrimeStationOne(ipAddressToTry, hostname, primeStationVersion);
                Timber.d("Found PrimeStationOne: " + primeStationOne);
                mPrimeStationOneList.add(primeStationOne);
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
        String[] gatewayIpOctets = gatewayIp.split(NetworkUtilities.IP_SEPARATOR_CHAR_MATCHER);
        String gatewayPrefix = gatewayIpOctets.length == 0 ? "" : gatewayIpOctets[0]
                + NetworkUtilities.IP_SEPARATOR_CHAR + gatewayIpOctets[1] + NetworkUtilities.IP_SEPARATOR_CHAR + gatewayIpOctets[2] + NetworkUtilities.IP_SEPARATOR_CHAR;
        Timber.d("gatewayIpOctets = " + Arrays.toString(gatewayIpOctets) + ", gatewayPrefix = "
                + gatewayPrefix + ", gatewayIp = " + gatewayIp + ", DhcpInfo = " + dhcpInfo);
        return gatewayPrefix;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mRvPiList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }
}
