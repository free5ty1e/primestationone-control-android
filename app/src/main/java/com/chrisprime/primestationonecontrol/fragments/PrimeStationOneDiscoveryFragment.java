package com.chrisprime.primestationonecontrol.fragments;

import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;
import com.chrisprime.primestationonecontrol.views.FoundPrimestationsRecyclerViewAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
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

public class PrimeStationOneDiscoveryFragment extends Fragment {

    private List<PrimeStationOne> mPrimeStationOneList = new ArrayList<>();
    private FoundPrimestationsRecyclerViewAdapter mFoundPrimestationsRecyclerViewAdapter;
    private Observable<String> mFindPiObservable;
    private Subscriber<String> mFindPiSubscriber;
    private Subscription mFindPiSubscription;

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

    @Bind(R.id.btn_find_pi)
    Button mBtnFindPi;

    @Bind(R.id.iv_fullscreen)
    ImageView mFullScreenImageView;

    @Bind(R.id.pb_centered)
    ProgressBar mCenteredProgressSpinner;

    @OnClick(R.id.btn_find_pi)
    void onFindPiButtonClicked(View view) {
        boolean isScanning = determineIsScanning();
        String scanStatusText = isScanning ? "Currently scanning!" : "Not currently scanning!";
        Timber.d("findPi button clicked! " + scanStatusText);
        if (isScanning) { //Cancel!
            mFindPiSubscriber.onCompleted();
        } else {
            mPrimeStationOneList.clear();
            mBtnFindPi.setText(R.string.button_find_pi_cancel_text);

            //TODO: Just put in a wifi wakelock, but for now this lazy thing works
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            final String gatewayPrefix = getCurrentGatewayPrefix();

            mFindPiObservable = Observable.create(
                    new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> sub) {
                            sub.onNext(checkForPrimeStationOnes(gatewayPrefix));
                        }
                    }
            )
//                .map(s -> s + " -Love, Chris")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mFindPiSubscriber = new Subscriber<String>() {

                @Override
                public void onNext(String s) {
                    Timber.d(".onNext(" + s + ")");
                }

                @Override
                public void onCompleted() {
                    //                findPiButton.setEnabled(true);
                    getActivity().runOnUiThread(() -> {
                        mBtnFindPi.setText(R.string.button_find_pi_text);
                        int numPrimestationsFound = mPrimeStationOneList.size();
                        mTvFoundPi.setText(numPrimestationsFound > 0 ?
                                numPrimestationsFound > 1 ? "Found " + numPrimestationsFound + " Primestations! xD" : "Found Primestation! :D"
                                : "None found :(");
                    });
                    unsubscribe();
                }

                @Override
                public void onError(Throwable e) {
                    Timber.e(e, "Error with subscriber: " + e + ": " + e.getMessage());
                }
            };
            mFindPiSubscription = mFindPiObservable.subscribe(mFindPiSubscriber);
        }
    }

    private boolean determineIsScanning() {
        return mFindPiSubscription != null && !mFindPiSubscriber.isUnsubscribed();
    }

    private String getHostname(String ipAddress) {
        String hostname = "hostname";
        InetAddress address;
        try {
            address = InetAddress.getByName(ipAddress);
            Timber.d("InetAddress for " + ipAddress + " = " + address);
            hostname = address.getCanonicalHostName();
            Timber.d("IP " + ipAddress + " hostname = " + hostname);
        } catch (Exception e) {
            Timber.e(e, "error obtaining hostname from " + ipAddress + ": " + e);
            mFindPiSubscriber.onError(e);
        }
        return hostname;
    }

    private String checkForPrimeStationOnes(String gatewayPrefix) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int lastIpOctetMin = Integer.valueOf(preferences.getString(getString(R.string.pref_key_override_ip_last_octet_min),
                getResources().getString(R.string.pref_default_ip_last_octet_min)));
        int lastIpOctetMax = Integer.valueOf(preferences.getString(getString(R.string.pref_key_override_ip_last_octet_max),
                getResources().getString(R.string.pref_default_ip_last_octet_max)));

        for (int ipLastOctetToTry = lastIpOctetMin;
             ipLastOctetToTry <= lastIpOctetMax; ipLastOctetToTry++) {
            if (determineIsScanning()) {  //Only if it wasn't cancelled!
                String ipAddressToTry = gatewayPrefix + ipLastOctetToTry;

                //Update status text to show current IP being scanned
                getActivity().runOnUiThread(() -> mTvFoundPi.setText(ipAddressToTry + "..."));
//                if (NetworkUtilities.ping(ipAddressToTry)) {          //Seems faster to just try each IP with SSH...
                String primeStationVersion = NetworkUtilities.sshCheckForPi(ipAddressToTry);
                if (primeStationVersion.length() > 0) {
                    String hostname = getHostname(ipAddressToTry);
                    String mac = "";
                    PrimeStationOne primeStationOne = new PrimeStationOne(ipAddressToTry, hostname, primeStationVersion, mac);
                    Timber.d("Found PrimeStationOne: " + primeStationOne);
                    mPrimeStationOneList.add(primeStationOne);
                }
//                }
            } else {
                return "cancelled";
            }
        }
        mFindPiSubscriber.onCompleted();
        return "success";
    }

    @NonNull
    private String getCurrentGatewayPrefix() {
        String gatewayPrefix;
        DhcpInfo dhcpInfo = NetworkUtilities.getDhcpInfo(getActivity());
        StringBuffer stringBuffer = new StringBuffer();
        NetworkUtilities.putAddress(stringBuffer, dhcpInfo.gateway);
        String gatewayIp = stringBuffer.toString();
        String[] gatewayIpOctets = gatewayIp.split(NetworkUtilities.IP_SEPARATOR_CHAR_MATCHER);
        String detectedGatewayPrefix = gatewayIpOctets.length == 0 ? "" : gatewayIpOctets[0]
                + NetworkUtilities.IP_SEPARATOR_CHAR + gatewayIpOctets[1] + NetworkUtilities.IP_SEPARATOR_CHAR + gatewayIpOctets[2] + NetworkUtilities.IP_SEPARATOR_CHAR;
        Timber.d("gatewayIpOctets = " + Arrays.toString(gatewayIpOctets) + ", gatewayPrefix = "
                + detectedGatewayPrefix + ", gatewayIp = " + gatewayIp + ", DhcpInfo = " + dhcpInfo);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean ipPrefixOverrideEnabled = preferences.getBoolean(getString(R.string.pref_key_override_ip_enable), false);

        if (ipPrefixOverrideEnabled) {
            gatewayPrefix = preferences.getString(getString(R.string.pref_key_override_ip_prefix),
                    getString(R.string.pref_default_ip_prefix)) + NetworkUtilities.IP_SEPARATOR_CHAR;
            Timber.d("IP Prefix override active, forcing prefix to: " + gatewayPrefix);
        } else {
            gatewayPrefix = detectedGatewayPrefix;
            Timber.d("IP Prefix override inactive, prefix autodetected: " + gatewayPrefix);
        }
        return gatewayPrefix;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mFullScreenImageView.setClickable(true);
        mFullScreenImageView.setOnClickListener(v -> {
            mFullScreenImageView.setVisibility(View.GONE);
            mCenteredProgressSpinner.setVisibility(View.GONE);
        });
        mRvPiList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFoundPrimestationsRecyclerViewAdapter = new FoundPrimestationsRecyclerViewAdapter(getActivity(), mPrimeStationOneList, uri -> {
            mFullScreenImageView.setVisibility(View.VISIBLE);
            mCenteredProgressSpinner.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(uri)
                    .into(mFullScreenImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Timber.d("Successfully loaded fullscreen image from uri: " + uri);
                            mCenteredProgressSpinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Timber.e("Failed to load fullscreen image from uri: " + uri);
                            mCenteredProgressSpinner.setVisibility(View.GONE);
                        }
                    });
        });
        mRvPiList.setAdapter(mFoundPrimestationsRecyclerViewAdapter);
        return rootView;
    }
}
