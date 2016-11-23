package com.chrisprime.primestationonecontrol.bases

import com.chrisprime.primestationonecontrol.utilities.TestUtilities
import org.junit.After

val mDefaultLanguage = "en"
val mDefaultCountry = "EN"
abstract class BaseLocaleTest @JvmOverloads protected constructor(language: String = mDefaultLanguage, country: String = mDefaultCountry): BaseUiTest(true) {

    var mLanguage = mDefaultLanguage
    var mCountry = mDefaultCountry

    init {
        mLanguage = language
        mCountry = country
    }

    override fun prepareBeforeActivityLaunchedFirstThing() {
        TestUtilities.setLocale(mLanguage, mCountry)
    }

    @After
    fun tearDown() {
        TestUtilities.setLocale(mDefaultLanguage, mDefaultCountry)  //Don't leave the locale set elsewhere for the remaining tests, that might confuse some of them
    }
}

