package com.chrisprime.primestationonecontrol.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.activities.PrimeStationOneControlActivity
import com.chrisprime.primestationonecontrol.dagger.Injector
import com.chrisprime.primestationonecontrol.events.PrimeStationsListUpdatedEvent
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.chrisprime.primestationonecontrol.utilities.FileUtilities
import com.chrisprime.primestationonecontrol.utilities.HostScanner
import com.chrisprime.primestationonecontrol.utilities.NetInfo
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities
import com.chrisprime.primestationonecontrol.views.FoundPrimestationsRecyclerViewAdapter
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_primestation_one_discovery.*
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class PrimeStationOneDiscoveryFragment : BaseFragment() {
    private var mPrimeStationOneList: MutableList<PrimeStationOne>? = null
    private var mFoundPrimestationsRecyclerViewAdapter: FoundPrimestationsRecyclerViewAdapter? = null
    private var mFindPiSubscriber: Subscriber<String>? = null
    private var mFindPiSubscription: Subscription? = null
    private var mActiveIpScans: MutableSet<String>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_primestation_one_discovery, container, false)
        Injector.applicationComponent.inject(this)
        initializeFoundPrimeStationsListFromJson()
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_pi_list.layoutManager = LinearLayoutManager(activity)
        discovery_empty_view.setOnButtonClick({ (activity as PrimeStationOneControlActivity).onNavigationDrawerItemSelected(PrimeStationOneControlActivity.NAVIGATION_INDEX_SETTINGS) })
        rv_pi_list.setEmptyView(discovery_empty_view)
        rv_pi_list.setProgressView(discovery_progressbar_layout)
        btn_find_pi.setOnClickListener { onFindPiButtonClicked(it) }
        updateDisplayedList()
    }

    fun onFindPiButtonClicked(view: View) {
        val isScanning = determineIsScanning()
        val scanStatusText = if (isScanning) "Currently scanning!" else "Not currently scanning!"
        Timber.d("findPi button clicked! " + scanStatusText)
        if (isScanning) { //Cancel!
            mFindPiSubscriber!!.onCompleted()
        } else {
            mPrimeStationOneList!!.clear()
            setUiScanning()

            //TODO: Just put in a wifi wakelock, but for now this lazy thing works
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            val gatewayPrefix = currentGatewayPrefix

            val mFindPiObservable = Observable.create(
                    Observable.OnSubscribe<kotlin.String> { sub -> sub.onNext(checkForPrimeStationOnes(gatewayPrefix)) }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

            mFindPiSubscriber = object : Subscriber<String>() {

                override fun onNext(s: String) {
                    Timber.d(".onNext($s)")
                }

                override fun onCompleted() {
                    //                findPiButton.setEnabled(true);
                    activity.runOnUiThread {
                        setUiIdle()
                        val numPrimestationsFound = mPrimeStationOneList!!.size
                        tv_found_pi!!.text = if (numPrimestationsFound > 0)
                            if (numPrimestationsFound > 1) "Found $numPrimestationsFound Primestations! xD" else "Found Primestation! :D"
                        else
                            "None found :("
                        FileUtilities.storeFoundPrimeStationsJson(activity, mPrimeStationOneList!!)
                        updateCurrentlyScanningAddress(getString(R.string.scan_finished_text))

                        //Clear lazy screen wakelock now that scan has completed
                        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                    unsubscribe()
                }

                override fun onError(e: Throwable) {
                    Timber.e(e, "Error with subscriber: " + e + ": " + e.message)
                }
            }
            mFindPiSubscription = mFindPiObservable.subscribe(mFindPiSubscriber!!)
        }
    }

    private fun setUiIdle() {
        btn_find_pi!!.setText(R.string.button_find_pi_text)
        rv_pi_list!!.setLoading(false)
    }

    private fun setUiScanning() {
        btn_find_pi!!.setText(R.string.button_find_pi_cancel_text)
        rv_pi_list!!.setLoading(true)
    }

    private fun determineIsScanning(): Boolean {
        return mFindPiSubscription != null && !mFindPiSubscriber!!.isUnsubscribed
    }

    private fun checkForPrimeStationOnes(gatewayPrefix: String): String {
        val fastMethodEnabled = mPreferenceStore.getBoolean(R.string.pref_key_discovery_method_fast_enable, R.bool.pref_default_discovery_method_fast_enable)
        if (fastMethodEnabled) {
            return checkForPrimeStationOnesFastMethod(gatewayPrefix)
        } else {
            return checkForPrimeStationOnesSlowMethod(gatewayPrefix)
        }
    }

    private fun checkForPrimeStationOnesFastMethod(gatewayPrefix: String): String {
        val lastIpOctetMin = lastIpOctetMin!!
        val lastIpOctetMax = lastIpOctetMax!!
        val startIp = NetInfo.getUnsignedLongFromIp(gatewayPrefix + lastIpOctetMin)
        val endIp = NetInfo.getUnsignedLongFromIp(gatewayPrefix + lastIpOctetMax)

        mActiveIpScans = HashSet<String>()
        for (currentIp in startIp..endIp) {
            val finalCurrentIp = currentIp
            if (determineIsScanning()) {  //Only if it wasn't cancelled!
                ipScanStarted(finalCurrentIp)
                safeRunOnIoThread({
                    val ipAddressString = NetInfo.getIpFromLongUnsigned(finalCurrentIp)
                    updateCurrentlyScanningAddress(ipAddressString)
                    val host = HostScanner(NetInfo.getIpFromLongUnsigned(finalCurrentIp)).scanForHost()
                    if (host == null) {
                        Timber.d("Dead host %s ignored!", ipAddressString)
                    } else {
                        Timber.d("Alive host %s found!  Checking to see if it's a Primestation...", ipAddressString)
                        checkIsPrimeStationOne(ipAddressString)
                    }
                    ipScanComplete(finalCurrentIp)
                })
            } else {
                return CANCELLED
            }
        }
        return SUCCESS
    }

    private val lastIpOctetMax: Int?
        get() = Integer.valueOf(mPreferenceStore.getString(R.string.pref_key_override_ip_last_octet_max, R.string.pref_default_ip_last_octet_max))

    private val lastIpOctetMin: Int?
        get() = Integer.valueOf(mPreferenceStore.getString(R.string.pref_key_override_ip_last_octet_min, R.string.pref_default_ip_last_octet_min))

    private fun ipScanStarted(ip: Long) {
        val ipString = NetInfo.getIpFromLongUnsigned(ip)
        mActiveIpScans!!.add(ipString)
        discovery_progressbar!!.max = mActiveIpScans!!.size
        discovery_progressbar!!.progress = 1
        updateCurrentlyScanningAddress(ipString)
        Timber.d(".ipScanStarted(%s), number of active scans remaining: %d", ipString, mActiveIpScans!!.size)
    }

    private fun ipScanComplete(ip: Long) {
        val ipString = NetInfo.getIpFromLongUnsigned(ip)
        mActiveIpScans!!.remove(ipString)
        discovery_progressbar!!.max = mActiveIpScans!!.size
        Timber.d(".ipScanComplete(%s), number of active scans remaining: %d", ipString, mActiveIpScans!!.size)
        if (mActiveIpScans!!.size == 0) {
            mFindPiSubscriber!!.onCompleted()
        } else if (mActiveIpScans!!.size < 5) {
            Timber.v(".ipScanComplete(%s): active scans remaining = %s", ipString, mActiveIpScans)
            if (mActiveIpScans!!.size > 0) {
                val ipAddressToTry = mActiveIpScans!!.toTypedArray()[0]
                @Suppress("SENSELESS_COMPARISON")
                if (ipAddressToTry == null) {
                    mFindPiSubscriber!!.onCompleted()
                } else {
                    updateCurrentlyScanningAddress(ipAddressToTry)
                }
            }
        }
    }

    private fun checkForPrimeStationOnesSlowMethod(gatewayPrefix: String): String {
        val lastIpOctetMin = lastIpOctetMin!!
        val lastIpOctetMax = lastIpOctetMax!!
        for (ipLastOctetToTry in lastIpOctetMin..lastIpOctetMax) {
            if (determineIsScanning()) {  //Only if it wasn't cancelled!
                checkIsPrimeStationOne(gatewayPrefix + ipLastOctetToTry)
            } else {
                return CANCELLED
            }
        }
        mFindPiSubscriber!!.onCompleted()
        return SUCCESS
    }

    private fun checkIsPrimeStationOne(ipAddressToTry: String) {
        //Update status text to show current IP being scanned
        updateCurrentlyScanningAddress(ipAddressToTry)
        //                if (NetworkUtilities.ping(ipAddressToTry)) {          //Seems faster to just try each IP with SSH...
        val primeStationOne = NetworkUtilities.sshCheckForPi(ipAddressToTry)
        if (primeStationOne != null) {
            mPrimeStationOneList!!.add(primeStationOne)
        }
    }

    private fun updateCurrentlyScanningAddress(ipAddressToTry: String) {
        activity.runOnUiThread { tv_found_pi!!.text = String.format("%s...", ipAddressToTry) }
    }

    private val currentGatewayPrefix: String
        get() {
            val gatewayPrefix: String
            val dhcpInfo = NetworkUtilities.getDhcpInfo(activity)
            val stringBuffer = StringBuffer()
            NetworkUtilities.putAddress(stringBuffer, dhcpInfo.gateway)
            val gatewayIp = stringBuffer.toString()
            val gatewayIpOctets = gatewayIp.split(NetworkUtilities.IP_SEPARATOR_CHAR_MATCHER.toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            val detectedGatewayPrefix = if (gatewayIpOctets.size == 0)
                ""
            else
                gatewayIpOctets[0] + NetworkUtilities.IP_SEPARATOR_CHAR + gatewayIpOctets[1] + NetworkUtilities.IP_SEPARATOR_CHAR + gatewayIpOctets[2] + NetworkUtilities.IP_SEPARATOR_CHAR
            Timber.d("gatewayIpOctets = " + Arrays.toString(gatewayIpOctets) + ", gatewayPrefix = "
                    + detectedGatewayPrefix + ", gatewayIp = " + gatewayIp + ", DhcpInfo = " + dhcpInfo)

            val ipPrefixOverrideEnabled = mPreferenceStore.getBoolean(R.string.pref_key_override_ip_enable, R.bool.pref_default_override_ip)

            if (ipPrefixOverrideEnabled) {
                gatewayPrefix = mPreferenceStore.getString(R.string.pref_key_override_ip_prefix, R.string.pref_default_ip_prefix) + NetworkUtilities.IP_SEPARATOR_CHAR
                Timber.d("IP Prefix override active, forcing prefix to: " + gatewayPrefix)
            } else {
                gatewayPrefix = detectedGatewayPrefix
                Timber.d("IP Prefix override inactive, prefix autodetected: " + gatewayPrefix)
            }
            return gatewayPrefix
        }

    private fun updateDisplayedList() {
        mFoundPrimestationsRecyclerViewAdapter = FoundPrimestationsRecyclerViewAdapter(mPrimeStationOneList)
        rv_pi_list!!.adapter = mFoundPrimestationsRecyclerViewAdapter
    }

    private fun initializeFoundPrimeStationsListFromJson() {
        //restore json from file and use as found primestations without requiring a scan, if any were stored:
        mPrimeStationOneList = FileUtilities.readJsonPrimestationList(activity)?.toMutableList()
        Timber.d("Deserialized json file into primeStationOneList: " + mPrimeStationOneList)
        if (mPrimeStationOneList == null) {
            mPrimeStationOneList = ArrayList<PrimeStationOne>()
        }
    }

    @Suppress("unused")
    @Subscribe
    fun answerPrimeStationsListUpdatedEvent(primeStationsListUpdatedEvent: PrimeStationsListUpdatedEvent) {
        Timber.d(".answerPrimeStationsListUpdatedEvent(): forcing update of primestation list to ensure data sync...")
        initializeFoundPrimeStationsListFromJson()
        updateDisplayedList()
    }

    companion object {

        val SUCCESS = "success"
        val CANCELLED = "cancelled"

        fun newInstance(): PrimeStationOneDiscoveryFragment {
            val fragment = PrimeStationOneDiscoveryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
