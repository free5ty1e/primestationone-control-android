package com.chrisprime.primestationonecontrol.dagger.modules

import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import dagger.Module
import dagger.Provides
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import timber.log.Timber
import javax.inject.Singleton

/**
 * Created by cpaian on 8/19/15.
 */
@Module
class MockWebServerModule {

    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer {
        if (mockWebServer == null) {
            Timber.d(".provideMockWebServer() sever was null, creating a fresh instance...")
            mockWebServer = MockWebServer()
            Timber.d(".provideMockWebServer() about to set dispatcher...")
            mockWebServer!!.setDispatcher(object : Dispatcher() {
                @Throws(InterruptedException::class)
                override fun dispatch(request: RecordedRequest): MockResponse? {
                    return null
                }
            })
            //            .setOverrideBaseMockUrl(mockWebServer.url("/").url().toString());
            PrimeStationOne.mockIpOverride = mockWebServer!!.hostName
        }
        return mockWebServer!!
    }

    companion object {

        var mockWebServer: MockWebServer? = null
    }
}
