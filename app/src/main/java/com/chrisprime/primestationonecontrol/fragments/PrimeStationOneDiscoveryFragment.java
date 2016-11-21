package com.chrisprime.primestationonecontrol.fragments;

import android.net.DhcpInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity;
import com.chrisprime.primestationonecontrol.dagger.Injector;
import com.chrisprime.primestationonecontrol.events.PrimeStationsListUpdatedEvent;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.FileUtilities;
import com.chrisprime.primestationonecontrol.utilities.HostBean;
import com.chrisprime.primestationonecontrol.utilities.HostScanner;
import com.chrisprime.primestationonecontrol.utilities.NetInfo;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;
import com.chrisprime.primestationonecontrol.views.DiscoveryEmptyView;
import com.chrisprime.primestationonecontrol.views.EmptyRecyclerView;
import com.chrisprime.primestationonecontrol.views.FoundPrimestationsRecyclerViewAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PrimeStationOneDiscoveryFragment extends BaseFragment {

    public static final String SUCCESS = "success";
    public static final String CANCELLED = "cancelled";
    private List<PrimeStationOne> mPrimeStationOneList;
    private FoundPrimestationsRecyclerViewAdapter mFoundPrimestationsRecyclerViewAdapter;
    private Subscriber<String> mFindPiSubscriber;
    private Subscription mFindPiSubscription;
    private Set<String> mActiveIpScans;

    public static PrimeStationOneDiscoveryFragment newInstance() {
        PrimeStationOneDiscoveryFragment fragment = new PrimeStationOneDiscoveryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.tv_found_pi)
    TextView mTvFoundPi;

    @Bind(R.id.rv_pi_list)
    EmptyRecyclerView mRvPiList;

    @Bind(R.id.discovery_empty_view)
    DiscoveryEmptyView mDiscoveryEmptyView;

    @Bind(R.id.btn_find_pi)
    Button mBtnFindPi;

    @Bind(R.id.discovery_progressbar)
    ProgressBar mProgressBar;

    @Bind(R.id.discovery_spinner)
    ProgressBar mSpinner;

    @Bind(R.id.discovery_progressbar_layout)
    View mProgressBars;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_primestation_one_discovery, container, false);
        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this, rootView);
        mRvPiList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDiscoveryEmptyView.setOnButtonClick(() -> {
            ((PrimeStationOneControlActivity) getActivity()).onNavigationDrawerItemSelected(PrimeStationOneControlActivity.NAVIGATION_INDEX_SETTINGS);
        });
        mRvPiList.setEmptyView(mDiscoveryEmptyView);
        mRvPiList.setProgressView(mProgressBars);
        initializeFoundPrimeStationsListFromJson();
        updateDisplayedList();
        return rootView;
    }

    @OnClick(R.id.btn_find_pi)
    void onFindPiButtonClicked(View view) {
        boolean isScanning = determineIsScanning();
        String scanStatusText = isScanning ? "Currently scanning!" : "Not currently scanning!";
        Timber.d("findPi button clicked! " + scanStatusText);
        if (isScanning) { //Cancel!
            mFindPiSubscriber.onCompleted();
        } else {
            mPrimeStationOneList.clear();
            setUiScanning();

            //TODO: Just put in a wifi wakelock, but for now this lazy thing works
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            final String gatewayPrefix = getCurrentGatewayPrefix();

            Observable<String> mFindPiObservable = Observable.create(
                    new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> sub) {
                            sub.onNext(checkForPrimeStationOnes(gatewayPrefix));
                        }
                    }
            )
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
                        setUiIdle();
                        int numPrimestationsFound = mPrimeStationOneList.size();
                        mTvFoundPi.setText(numPrimestationsFound > 0 ?
                                numPrimestationsFound > 1 ? "Found " + numPrimestationsFound + " Primestations! xD" : "Found Primestation! :D"
                                : "None found :(");
                        FileUtilities.storeFoundPrimeStationsJson(getActivity(), mPrimeStationOneList);


                        //Clear lazy screen wakelock now that scan has completed
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    private void setUiIdle() {
        mBtnFindPi.setText(R.string.button_find_pi_text);
        mRvPiList.setLoading(false);
    }

    private void setUiScanning() {
        mBtnFindPi.setText(R.string.button_find_pi_cancel_text);
        mRvPiList.setLoading(true);
    }

    private boolean determineIsScanning() {
        return mFindPiSubscription != null && !mFindPiSubscriber.isUnsubscribed();
    }

    private String checkForPrimeStationOnes(String gatewayPrefix) {
        boolean fastMethodEnabled = mPreferenceStore.getBoolean(R.string.pref_key_discovery_method_fast_enable, R.bool.pref_default_discovery_method_fast_enable);
        if (fastMethodEnabled) {
            return checkForPrimeStationOnesFastMethod(gatewayPrefix);
        } else {
            return checkForPrimeStationOnesSlowMethod(gatewayPrefix);
        }
    }

    private String checkForPrimeStationOnesFastMethod(String gatewayPrefix) {
        int lastIpOctetMin = getLastIpOctetMin();
        int lastIpOctetMax = getLastIpOctetMax();
        long startIp = NetInfo.getUnsignedLongFromIp(gatewayPrefix + lastIpOctetMin);
        long endIp = NetInfo.getUnsignedLongFromIp(gatewayPrefix + lastIpOctetMax);

        mActiveIpScans = new HashSet<>();
        for (long currentIp = startIp; currentIp <= endIp; currentIp++) {
            long finalCurrentIp = currentIp;
            if (determineIsScanning()) {  //Only if it wasn't cancelled!
                ipScanStarted(finalCurrentIp);
                safeRunOnIoThread(() -> {
                    String ipAddressString = NetInfo.getIpFromLongUnsigned(finalCurrentIp);
                    updateCurrentlyScanningAddress(ipAddressString);
                    HostBean host = new HostScanner(NetInfo.getIpFromLongUnsigned(finalCurrentIp)).scanForHost();
                    if (host == null) {
                        Timber.d("Dead host %s ignored!", ipAddressString);
                    } else {
                        Timber.d("Alive host %s found!  Checking to see if it's a Primestation...", ipAddressString);
                        checkIsPrimeStationOne(ipAddressString);
                    }
                    ipScanComplete(finalCurrentIp);
                });
            } else {
                return CANCELLED;
            }
        }
        return SUCCESS;
    }

    private Integer getLastIpOctetMax() {
        return Integer.valueOf(mPreferenceStore.getString(R.string.pref_key_override_ip_last_octet_max, R.string.pref_default_ip_last_octet_max));
    }

    private Integer getLastIpOctetMin() {
        return Integer.valueOf(mPreferenceStore.getString(R.string.pref_key_override_ip_last_octet_min, R.string.pref_default_ip_last_octet_min));
    }

    private void ipScanStarted(long ip) {
        String ipString = NetInfo.getIpFromLongUnsigned(ip);
        mActiveIpScans.add(ipString);
        mProgressBar.setMax(mActiveIpScans.size());
        mProgressBar.setProgress(1);
        updateCurrentlyScanningAddress(ipString);
        Timber.d(".ipScanStarted(%s), number of active scans remaining: %d", ipString, mActiveIpScans.size());
    }

    private void ipScanComplete(long ip) {
        String ipString = NetInfo.getIpFromLongUnsigned(ip);
        mActiveIpScans.remove(ipString);
        mProgressBar.setMax(mActiveIpScans.size());
        Timber.d(".ipScanComplete(%s), number of active scans remaining: %d", ipString, mActiveIpScans.size());
        if (mActiveIpScans.size() == 0) {
            mFindPiSubscriber.onCompleted();
            updateCurrentlyScanningAddress(getString(R.string.scan_finished_text));
        } else if (mActiveIpScans.size() < 5) {
            Timber.v(".ipScanComplete(%s): active scans remaining = %s", ipString, mActiveIpScans);
            if (mActiveIpScans.size() > 0) {
                updateCurrentlyScanningAddress((String) mActiveIpScans.toArray()[0]);
            }
        }
    }

    private String checkForPrimeStationOnesSlowMethod(String gatewayPrefix) {
        int lastIpOctetMin = getLastIpOctetMin();
        int lastIpOctetMax = getLastIpOctetMax();
        for (int ipLastOctetToTry = lastIpOctetMin;
             ipLastOctetToTry <= lastIpOctetMax; ipLastOctetToTry++) {
            if (determineIsScanning()) {  //Only if it wasn't cancelled!
                checkIsPrimeStationOne(gatewayPrefix + ipLastOctetToTry);
            } else {
                return CANCELLED;
            }
        }
        mFindPiSubscriber.onCompleted();
        return SUCCESS;
    }

    private void checkIsPrimeStationOne(String ipAddressToTry) {
        //Update status text to show current IP being scanned
        updateCurrentlyScanningAddress(ipAddressToTry);
//                if (NetworkUtilities.ping(ipAddressToTry)) {          //Seems faster to just try each IP with SSH...
        PrimeStationOne primeStationOne = NetworkUtilities.sshCheckForPi(ipAddressToTry);
        if (primeStationOne != null) {
            mPrimeStationOneList.add(primeStationOne);
        }
    }

    private void updateCurrentlyScanningAddress(String ipAddressToTry) {
        getActivity().runOnUiThread(() -> mTvFoundPi.setText(String.format("%s...", ipAddressToTry)));
    }

    @NonNull
    private String getCurrentGatewayPrefix() {
        String gatewayPrefix;
        DhcpInfo dhcpInfo = NetworkUtilities.getDhcpInfo(getActivity());
        StringBuffer stringBuffer = new StringBuffer();
        NetworkUtilities.putAddress(stringBuffer, dhcpInfo.gateway);
        String gatewayIp = stringBuffer.toString();
        String[] gatewayIpOctets = gatewayIp.split(NetworkUtilities.getIP_SEPARATOR_CHAR_MATCHER());
        String detectedGatewayPrefix = gatewayIpOctets.length == 0 ? "" : gatewayIpOctets[0]
                + NetworkUtilities.getIP_SEPARATOR_CHAR() + gatewayIpOctets[1] + NetworkUtilities.getIP_SEPARATOR_CHAR() + gatewayIpOctets[2] + NetworkUtilities.getIP_SEPARATOR_CHAR();
        Timber.d("gatewayIpOctets = " + Arrays.toString(gatewayIpOctets) + ", gatewayPrefix = "
                + detectedGatewayPrefix + ", gatewayIp = " + gatewayIp + ", DhcpInfo = " + dhcpInfo);

        boolean ipPrefixOverrideEnabled = mPreferenceStore.getBoolean(R.string.pref_key_override_ip_enable, R.bool.pref_default_override_ip);

        if (ipPrefixOverrideEnabled) {
            gatewayPrefix = mPreferenceStore.getString(R.string.pref_key_override_ip_prefix, R.string.pref_default_ip_prefix) + NetworkUtilities.getIP_SEPARATOR_CHAR();
            Timber.d("IP Prefix override active, forcing prefix to: " + gatewayPrefix);
        } else {
            gatewayPrefix = detectedGatewayPrefix;
            Timber.d("IP Prefix override inactive, prefix autodetected: " + gatewayPrefix);
        }
        return gatewayPrefix;
    }

    private void updateDisplayedList() {
        mFoundPrimestationsRecyclerViewAdapter = new FoundPrimestationsRecyclerViewAdapter(mPrimeStationOneList);
        mRvPiList.setAdapter(mFoundPrimestationsRecyclerViewAdapter);
    }

    private void initializeFoundPrimeStationsListFromJson() {
        //restore json from file and use as found primestations without requiring a scan, if any were stored:
        mPrimeStationOneList = FileUtilities.readJsonPrimestationList(getActivity());
        Timber.d("Deserialized json file into primeStationOneList: " + mPrimeStationOneList);
        if (mPrimeStationOneList == null) {
            mPrimeStationOneList = new ArrayList<>();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void answerPrimeStationsListUpdatedEvent(PrimeStationsListUpdatedEvent primeStationsListUpdatedEvent) {
        Timber.d(".answerPrimeStationsListUpdatedEvent(): forcing update of primestation list to ensure data sync...");
        initializeFoundPrimeStationsListFromJson();
        updateDisplayedList();
    }
}
