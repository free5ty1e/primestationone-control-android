package com.chrisprime.primestationonecontrol.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parceler.Parcels;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by cpaian on 7/20/15.
 */
public class PrimeStationOneTest {

    @Before
    public void setUp() throws Exception {

    }

    /**
     * Testing involving a Parcelable requires an actual fucking connectedAndroidTest to proceed, so here we are:
     * @throws Exception
     */
    @Test
    public void testPrimeStationOneModelParcelable() throws Exception {
        PrimeStationOne primeStationOneParcelSource = new PrimeStationOne("192.168.1.50","primestationpi2.home","v0.9999beta");
        assertNotNull(primeStationOneParcelSource);

        String ip = primeStationOneParcelSource.getIpAddress();

        Parcelable parcelable = Parcels.wrap(primeStationOneParcelSource);
        assertNotNull(parcelable);

        Parcel parcel = Parcel.obtain();
        assertNotNull(parcel);
        parcelable.writeToParcel(parcel, 0);
        assertNotNull(parcel);
        parcel.setDataPosition(0);

        Field creatorField = parcelable.getClass().getField("CREATOR");
        Parcelable newParcelable = (Parcelable) ((Parcelable.Creator) creatorField.get(parcelable)).createFromParcel(parcel);

        PrimeStationOne primeStationOneUnparcelDestination = Parcels.unwrap(newParcelable);
        assertNotNull(primeStationOneUnparcelDestination);

        assertEquals(primeStationOneUnparcelDestination.getIpAddress(), ip);
    }

    @After
    public void tearDown() throws Exception {

    }

}

