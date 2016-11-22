package com.chrisprime.primestationonecontrol.dagger.modules

import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.utilities.PreferenceStore

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * Created by cpaian on 8/19/15.
 */
@Module
class PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(PrimeStationOneControlApplication.instance.applicationContext)
    }


    @Provides
    @Singleton
    fun providePreferenceStore(): PreferenceStore {
        return PreferenceStore.instance
    }

}
