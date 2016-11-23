package com.chrisprime.primestationonecontrol.dagger

import com.chrisprime.primestationonecontrol.bases.BaseUiTest
import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModuleInstrumentationTest
import com.chrisprime.primestationonecontrol.dagger.modules.MockWebServerModule
import com.chrisprime.primestationonecontrol.dagger.modules.PreferencesModule
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by cpaian on 8/24/16.
 */
@Singleton
@Component(modules = arrayOf(ApplicationContextModuleInstrumentationTest::class, PreferencesModule::class, MockWebServerModule::class, ThreadPoolModule::class))
interface ApplicationComponentInstrumentationTest : ApplicationComponent {

    fun inject(baseUiTest: BaseUiTest)
}
