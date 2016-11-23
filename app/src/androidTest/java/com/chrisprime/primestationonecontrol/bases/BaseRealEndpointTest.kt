package com.chrisprime.primestationonecontrol.bases

/**
 * Base test to allow testing with our mocks disabled
 * Created by cpaian on 10/3/16.
 */
abstract class BaseRealEndpointTest @JvmOverloads protected constructor(clearUserDataBeforeLaunch: Boolean = false) : BaseUiTest(clearUserDataBeforeLaunch) {

    override fun shouldMockEndpoints(): Boolean {
        return false
    }
}
