package com.chrisprime.primestationonecontrol.utilities

/**
 * Created by cpaian on 4/24/16.
 */
object StringUtilities {
    fun isEmpty(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.isEmpty()
    }
}
