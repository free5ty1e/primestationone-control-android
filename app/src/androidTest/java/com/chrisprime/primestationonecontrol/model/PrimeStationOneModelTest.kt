package com.chrisprime.primestationonecontrol.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.parceler.Parcels

/**
 * Created by cpaian on 7/20/15.
 */
class PrimeStationOneModelTest {

    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    /**
     * Testing involving a Parcelable requires an actual fucking connectedAndroidTest to proceed, so here we are:

     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testPrimeStationOneModelParcelable() {
        val primeStationOneParcelSource: PrimeStationOne = PrimeStationOne("192.168.1.50", "primestationpi2.home", "v0.9999beta", "00-00-00-00-00-00", "pi", false, null, null, "pi", "primestation1")
        assertNotNull(primeStationOneParcelSource)

        val ip = primeStationOneParcelSource.ipAddress

        val parcelable = Parcels.wrap(primeStationOneParcelSource)
        assertNotNull(parcelable)

//        val parcel = Parcel.obtain()
//        assertNotNull(parcel)
//        parcelable.writeToParcel(parcel, 0)
//        assertNotNull(parcel)
//        parcel.setDataPosition(0)

//        val creatorField = parcelable.javaClass.getField("CREATOR")
//        val newParcelable = (creatorField.get(parcelable) as Parcelable.Creator<*>).createFromParcel(parcel) as Parcelable

        val primeStationOneUnparcelDestination = Parcels.unwrap<PrimeStationOne>(parcelable)
        assertNotNull(primeStationOneUnparcelDestination)

        assertEquals(primeStationOneUnparcelDestination.ipAddress, ip)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {

    }

}

