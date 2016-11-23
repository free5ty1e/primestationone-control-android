package com.chrisprime.primestationonecontrol.dagger.modules

import android.content.Context
import android.support.test.InstrumentationRegistry
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationContextModuleInstrumentationTest {

    @Provides
    @Singleton
    fun applicationContext(): Context {
        return InstrumentationRegistry.getContext()
    }
}
