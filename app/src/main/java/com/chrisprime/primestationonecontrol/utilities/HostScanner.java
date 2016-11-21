package com.chrisprime.primestationonecontrol.utilities;

import com.chrisprime.netscan.network.HardwareAddress;
import com.chrisprime.netscan.network.HostBean;
import com.chrisprime.netscan.network.NetInfo;
import com.chrisprime.netscan.network.RateControl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import timber.log.Timber;

/**
 * Created by cpaian on 11/20/16.
 */
public class HostScanner {

    private final static int[] DPORTS = { 139, 445, 22, 80 };

    private final String mAddr;
    private final RateControl mRateControl;
    private final static int HOSTS_BETWEEN_RATE_ADAPTS = 5;

    private static int sNumHostsScanned = 0;

    public HostScanner(String addr) {
        mAddr = addr;
        mRateControl = new RateControl();
    }

    public HostBean scanForHost() {
        Timber.d("run="+ mAddr);
        // Create host object
        HostBean host = new HostBean();
        host.responseTime = getRate();
        host.ipAddress = mAddr;
        try {
            InetAddress h = InetAddress.getByName(mAddr);
            // Rate control check
            if (mRateControl.indicator != null && ++sNumHostsScanned % HOSTS_BETWEEN_RATE_ADAPTS == 0) {
                mRateControl.adaptRate();
            }
            // Arp Check #1
            host.hardwareAddress = HardwareAddress.getHardwareAddress(mAddr);
            if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                Timber.i( "found using arp #1 "+ mAddr);
                return host;
            }
            // Native InetAddress check
            if (h.isReachable(getRate())) {
                Timber.i( "found using InetAddress ping "+ mAddr);
                // Set indicator and get a rate
                if (mRateControl.indicator == null) {
                    mRateControl.indicator = mAddr;
                    mRateControl.adaptRate();
                }
                return host;
            }
            // Arp Check #2
            host.hardwareAddress = HardwareAddress.getHardwareAddress(mAddr);
            if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                Timber.i( "found using arp #2 "+ mAddr);
                return host;
            }
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
            host.hardwareAddress = HardwareAddress.getHardwareAddress(mAddr);
            if(!NetInfo.NOMAC.equals(host.hardwareAddress)){
                Timber.i( "found using arp #3 "+ mAddr);
                return host;
            }
            return null;

        } catch (IOException e) {
            Timber.e( e.getMessage());
            return null;
        }
    }

    private int getRate() {
        return mRateControl.rate;
    }
}
