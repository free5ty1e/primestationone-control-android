package com.chrisprime.primestationonecontrol.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.R
import timber.log.Timber

class PrimeStationOneCloudBackupControlsFragment : PrimeStationOneBaseSshCommanderFragment() {

    @Bind(R.id.sv_status)
    lateinit var mSvStatus: ScrollView

    @Bind(R.id.tv_status)
    lateinit var mTvStatus: TextView

    @Bind(R.id.ll_button_container)
    lateinit var mButtonContainer: LinearLayout

    @OnClick(R.id.button_logout_of_mega)
    internal fun onMegaLogoutButtonClicked(view: View) {
        Timber.d("Mega login button clicked!")
        sendCommandToCurrentPrimeStationOne("megaCloudBakClearLogin.sh", true, mTvStatus, mTvStatus, mSvStatus)
    }

    @OnClick(R.id.button_force_backup_to_mega)
    internal fun onMegaForceBackupOverwriteButtonClicked(view: View) {
        Timber.d("Mega force backup overwrite button clicked!")
        sendCommandToCurrentPrimeStationOne("megaCloudBackupSaveStatesAndSrams.sh", true, mTvStatus, mTvStatus, mSvStatus)
    }

    @OnClick(R.id.button_force_restore_from_mega)
    internal fun onMegaForceRestoreFromCloudOverwriteLocalButtonClicked(view: View) {
        Timber.d("Mega force restore overwrite local button clicked!")
        sendCommandToCurrentPrimeStationOne("megaCloudRestoreSaveStatesAndSrams.sh", true, mTvStatus, mTvStatus, mSvStatus)
    }

    @OnClick(R.id.button_intelligent_cloud_save_sync)
    internal fun onMegaIntelligentCloudSaveSyncButtonClicked(view: View) {
        Timber.d("Mega intelligent cloud save sync button clicked!")
        sendCommandToCurrentPrimeStationOne("megaCloudSyncSaveStatesAndSrams.sh", true, mTvStatus, mTvStatus, mSvStatus)
    }

    @OnClick(R.id.button_login_to_mega)
    internal fun onMegaLoginButtonClicked(view: View) {
        Timber.d("Mega login button clicked!")

        //Pop up dialog boxes allowing enter of username and password, prepopulated with preference (settable in settings), and stored as preference after press OK
        val primeStationOne = PrimeStationOneControlApplication.instance.currentPrimeStationOne
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        primeStationOne!!.megaEmail = preferences.getString(getString(R.string.pref_key_mega_login_email),
                getString(R.string.pref_default_mega_login_email))
        primeStationOne.megaPassword = preferences.getString(getString(R.string.pref_key_mega_login_password),
                getString(R.string.pref_default_mega_login_password))

        MaterialDialog.Builder(activity).title("Login to MEGA").content("Enter login email for Mega.co.nz...").positiveText("NEXT >").negativeText("CANCEL").input("login email", primeStationOne.megaEmail, false) { materialDialog, charSequence ->
            Timber.d("email input: " + charSequence)
            primeStationOne.megaEmail = charSequence.toString()
            MaterialDialog.Builder(activity).title("Login to MEGA").content("Enter password for Mega.co.nz...").positiveText("LOGIN >").negativeText("CANCEL").inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD).input("login password", primeStationOne.megaPassword, false) { materialDialog1, charSequence1 ->
                Timber.d("password input: " + charSequence1)
                primeStationOne.megaPassword = charSequence1.toString()
                primeStationOne.updateStoredPrimestation(activity)
                if (primeStationOne.megaEmail == preferences.getString(getString(R.string.pref_key_mega_login_email), "") && primeStationOne.megaPassword == preferences.getString(getString(R.string.pref_key_mega_login_password), "")) {
                    Timber.d("Mega email and password match current preferences, no need to store...")
                } else {
                    MaterialDialog.Builder(activity).title("Save MEGA login on phone?").content("Save this MEGA login on this phone as the preferred to pre-fill for any future PrimeStation cloud logins?").positiveText("OK").negativeText("NO THANKS").callback(object : MaterialDialog.ButtonCallback() {
                        override fun onPositive(dialog: MaterialDialog?) {
                            super.onPositive(dialog)
                            Timber.d("Mega email and password don't match current preferences, user requested to store in preferences...")
                            val editor = preferences.edit()
                            editor.putString(getString(R.string.pref_key_mega_login_email), primeStationOne.megaEmail)
                            editor.putString(getString(R.string.pref_key_mega_login_password), primeStationOne.megaPassword)
                            editor.commit()
                        }
                    }).show()
                }
                sendCommandToCurrentPrimeStationOne("echo \"Creating your .megarc file from provided email \$email and not printing your password out of courtesy, you are welcome...\"\n" +
                        "cat > /home/pi/.megarc << _EOF_\n" +
                        "[Login]\n" +
                        "Username = " + primeStationOne.megaEmail + "\n" +
                        "Password = " + primeStationOne.megaPassword + "\n" +
                        "_EOF_\n" +
                        "cat ~/.megarc ; megals", true, mTvStatus, mTvStatus, mSvStatus)
            }.show()
        }.show()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_primestation_one_cloud_backup_controls, container, false)
        ButterKnife.bind(this, rootView)
        initializeCommander(mButtonContainer)
        return rootView
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): PrimeStationOneCloudBackupControlsFragment {
            val fragment = PrimeStationOneCloudBackupControlsFragment()
            val args = Bundle()
            //        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.arguments = args
            return fragment
        }
    }
}
