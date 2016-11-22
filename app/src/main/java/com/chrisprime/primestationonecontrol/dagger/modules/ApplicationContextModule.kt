package com.chrisprime.primestationonecontrol.dagger.modules


import android.content.Context

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class ApplicationContextModule(private val sApplication: PrimeStationOneControlApplication) {

    @Provides
    @Singleton
    fun application(): PrimeStationOneControlApplication {
        return sApplication
    }

    @Provides
    @Singleton
    fun applicationContext(): Context {
        return sApplication.applicationContext
    }
}
