package com.chrisprime.primestationonecontrol.bases

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

/**
 * Use this test as your base class if you'd like to modify any of the default mocks for your test cases
 * Created by cpaian on 10/3/16.
 */
abstract class BaseAlternateMocksTest @JvmOverloads protected constructor(clearUserDataBeforeLaunch: Boolean = false) : BaseUiTest(clearUserDataBeforeLaunch) {

    override fun prepareBeforeActivityLaunchedLastThing() { //This occurs far before the @Before annotation and is required if one wants to modify any mock and not just those after the Minimum Version Service call
/*
        val MockWebDispatcher: MockWebDispatcher = object: MockWebDispatcher() {
            override fun dispatch(recordedRequest: RecordedRequest): MockResponse {
                val mockResponse = super.dispatch(recordedRequest)
                modifyResponse(mockResponse, recordedRequest)
                return mockResponse
            }
        }
        mMockWebServer!!.setDispatcher(MockWebDispatcher)
*/
    }

    /**
     * Override this function and modify the mockresponse based on the recordedrequest with your own logic
     * Allow the default value to pass through to ensure all the basic mocks are still in place.
     */
    abstract fun modifyResponse(mockResponse: MockResponse, recordedRequest: RecordedRequest)

    override fun cleanUpAfterActivityFinishedLastThing() {
//        mMockWebServer!!.setDispatcher(MockWebDispatcher())
    }
}
