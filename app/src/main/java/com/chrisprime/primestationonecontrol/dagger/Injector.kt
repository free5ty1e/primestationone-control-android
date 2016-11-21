package com.chrisprime.primestationonecontrol.dagger

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.dagger.modules.ApplicationContextModule
import com.chrisprime.primestationonecontrol.dagger.modules.ThreadPoolModule

class Injector internal constructor() {
    companion object {

        @SuppressWarnings("WeakerAccess")
        private var sApplicationComponent: ApplicationComponent? = null

        fun initializeApplicationComponent(primeStationOneControlApplication: PrimeStationOneControlApplication) {
            sApplicationComponent = DaggerApplicationComponent.builder().applicationContextModule(ApplicationContextModule(primeStationOneControlApplication)).threadPoolModule(ThreadPoolModule()).build()
        }

        val applicationComponent: ApplicationComponent
            get() {
                if (sApplicationComponent == null) {
                    throw NullPointerException(".getApplicationComponent(): sApplicationComponent is null!  Injection broken...")
                }
                return sApplicationComponent!!
            }
    }
}
