package com.chrisprime.primestationonecontrol.activities

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.RingtonePreference
import android.text.TextUtils

import com.chrisprime.primestationonecontrol.R


/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
   * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
   * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : BaseEventBusPreferenceActivity() {


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setupSimplePreferencesScreen()
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private fun setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general)

        /*
        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        // Add 'data and sync' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_data_sync);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_data_sync);
*/

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_override_ip_prefix)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_override_ip_last_octet_min)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_override_ip_last_octet_max)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_mega_login_email)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_custom_pi_username)))
        //        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        //        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this) && !isSimplePreferences(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target)
        }
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return (GeneralPreferenceFragment::class.java.name == fragmentName ||
                DataSyncPreferenceFragment::class.java.name == fragmentName ||
                NotificationPreferenceFragment::class.java.name == fragmentName)
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_override_ip_prefix)))
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_override_ip_last_octet_min)))
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_override_ip_last_octet_max)))
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_mega_login_email)))
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class NotificationPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_notification)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"))
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class DataSyncPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_data_sync)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"))
        }
    }

    companion object {
        /**
         * Determines whether to always show the simplified settings UI, where
         * settings are presented in a single list. When false, settings are shown
         * as a master/detail two-pane view on tablets. When true, a single pane is
         * shown on tablets.
         */
        private val ALWAYS_SIMPLE_PREFS = false

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Determines whether the simplified settings UI should be shown. This is
         * true if this is forced via [.ALWAYS_SIMPLE_PREFS], or the device
         * doesn't have newer APIs like [PreferenceFragment], or the device
         * doesn't have an extra-large screen. In these cases, a single-pane
         * "simplified" settings UI should be shown.
         */
        private fun isSimplePreferences(context: Context): Boolean {
            return ALWAYS_SIMPLE_PREFS
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                    || !isXLargeTablet(context)
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener: Preference.OnPreferenceChangeListener = Preference.OnPreferenceChangeListener() { preference, value ->
            val stringValue = value.toString()

            /*        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else*/ if (preference is RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to 'silent' (no ringtone).
                preference.setSummary(R.string.pref_ringtone_silent)

            } else {
                val ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue))

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null)
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    val name = ringtone.getTitle(preference.getContext())
                    preference.setSummary(name)
                }
            }

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.summary = stringValue
        }
            true
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, ""))
        }
    }
}
