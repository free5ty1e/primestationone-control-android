package com.chrisprime.primestationonecontrol.utilities

import java.io.IOException
import java.net.InetAddress

import timber.log.Timber

/**
 * Created by cpaian on 11/20/16.
 */
class HostScanner(private val mAddr: String) {
    private val mRateControl: RateControl

    init {
        mRateControl = RateControl()
    }

    fun scanForHost(): HostBean? {
        Timber.d(".scanForHost(): scanning %s", mAddr)
        // Create host object
        val host = HostBean()
        host.responseTime = rate
        host.ipAddress = mAddr
        try {
            val h = InetAddress.getByName(mAddr)
            // Rate control check
            if (mRateControl.indicator != null && ++sNumHostsScanned % HOSTS_BETWEEN_RATE_ADAPTS == 0) {
                mRateControl.adaptRate()
            }
            /*
            // Arp Check #1
            host.hardwareAddress = HardwareAddress.getHardwareAddress(mAddr);
            if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                Timber.i( "found using arp #1 "+ mAddr);
                return host;
            }
*/
            // Native InetAddress check
            if (h.isReachable(rate)) {
                Timber.i("found using InetAddress ping " + mAddr)
                // Set indicator and get a rate
                if (mRateControl.indicator == null) {
                    mRateControl.indicator = mAddr
                    mRateControl.adaptRate()
                }
                return host
            }
            /*
            // Arp Check #2
            host.hardwareAddress = HardwareAddress.getHardwareAddress(mAddr);
            if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                Timber.i( "found using arp #2 "+ mAddr);
                return host;
            }
*/
            // Custom check
            //            int port;
            //            // TODO: Get ports from options
            //            Socket s = new Socket();
            //            for (int i = 0; i < DPORTS.length; i++) {
            //                try {
            //                    s.bind(null);
            //                    s.connect(new InetSocketAddress(mAddr, DPORTS[i]), getRate());
            //                    Timber.v( "found using TCP connect "+ mAddr +" on port=" + DPORTS[i]);
            //                } catch (IOException | IllegalArgumentException e) {
            //                } finally {
            //                    try {
            //                        s.close();
            //                    } catch (Exception e){
            //                    }
            //                }
            //            }

            /*
                if ((port = Reachable.isReachable(h, getRate())) > -1) {
                    Timber.v( "used Network.Reachable object, "+mAddr+" port=" + port);
                    publish(host);
                    return;
                }
                */
            // Arp Check #3
            /*
            host.hardwareAddress = HardwareAddress.getHardwareAddress(mAddr);
            if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                Timber.i( "found using arp #3 "+ mAddr);
                return host;
            }
*/
            return null

        } catch (e: IOException) {
            Timber.e("HostScanner error scanning %s: %s", mAddr, e.message)
            return null
        }

    }

    private val rate: Int
        get() = mRateControl.rate

    companion object {

        private val DPORTS = intArrayOf(139, 445, 22, 80)
        private val HOSTS_BETWEEN_RATE_ADAPTS = 5

        private var sNumHostsScanned = 0
    }
}
