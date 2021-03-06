package com.chrisprime.primestationonecontrol.dagger

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModule
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule

open class Injector {
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

        fun initializeApplicationComponent(primeStationOneControlApplication: PrimeStationOneControlApplication) {
            sApplicationComponent = DaggerApplicationComponent.builder().applicationContextModule(ApplicationContextModule(primeStationOneControlApplication)).threadPoolModule(ThreadPoolModule()).build()
        }

    }
}
