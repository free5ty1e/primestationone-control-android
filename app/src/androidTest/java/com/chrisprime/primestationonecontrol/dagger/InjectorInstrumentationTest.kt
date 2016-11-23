package com.chrisprime.primestationonecontrol.dagger

import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModuleInstrumentationTest
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule

class InjectorInstrumentationTest : Injector() {
    companion object {

        //Unfortunately, the below two declaration blocks have to be duplicated in the InjectorInstrumentationTest as I'm not sure how to extend a companion object...
        @SuppressWarnings("WeakerAccess")
        var sApplicationComponent: ApplicationComponent? = null

        val applicationComponent: ApplicationComponent
            get() {
                if (sApplicationComponent == null) {
                    throw NullPointerException(".getApplicationComponent(): sApplicationComponent is null!  Injection broken...")
                }
                return sApplicationComponent!!
            }

        fun initializeApplicationComponent() {
            sApplicationComponent = DaggerApplicationComponentInstrumentationTest.builder()
                    .applicationContextModuleInstrumentationTest(ApplicationContextModuleInstrumentationTest())
                    .threadPoolModule(ThreadPoolModule())
                    .build()
        }
    }
}
