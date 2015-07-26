package com.chrisprime.primestationonecontrol.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PrimeStationOneCloudBackupControlsFragment extends PrimeStationOneBaseSshCommanderFragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PrimeStationOneCloudBackupControlsFragment newInstance() {
        PrimeStationOneCloudBackupControlsFragment fragment = new PrimeStationOneCloudBackupControlsFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.button_logout_of_mega)
    void onMegaLogoutButtonClicked(View view) {
        Timber.d("Mega login button clicked!");
        sendCommandToCurrentPrimeStationOne("megaCloudBakClearLogin.sh", true, mTvStatus);
    }

    @OnClick(R.id.button_force_backup_to_mega)
    void onMegaForceBackupOverwriteButtonClicked(View view) {
        Timber.d("Mega force backup overwrite button clicked!");
        sendCommandToCurrentPrimeStationOne("megaCloudBackupSaveStatesAndSrams.sh", true, mTvStatus);
    }

    @OnClick(R.id.button_force_restore_from_mega)
    void onMegaForceRestoreFromCloudOverwriteLocalButtonClicked(View view) {
        Timber.d("Mega force restore overwrite local button clicked!");
        sendCommandToCurrentPrimeStationOne("megaCloudRestoreSaveStatesAndSrams.sh", true, mTvStatus);
    }

    @OnClick(R.id.button_intelligent_cloud_save_sync)
    void onMegaIntelligentCloudSaveSyncButtonClicked(View view) {
        Timber.d("Mega intelligent cloud save sync button clicked!");
        sendCommandToCurrentPrimeStationOne("megaCloudSyncSaveStatesAndSrams.sh", true, mTvStatus);
    }

    @OnClick(R.id.button_login_to_mega)
    void onMegaLoginButtonClicked(View view) {
        Timber.d("Mega login button clicked!");

        //Pop up dialog boxes allowing enter of username and password, prepopulated with preference (settable in settings), and stored as preference after press OK
        PrimeStationOne primeStationOne = PrimeStationOneControlApplication.getInstance().getCurrentPrimeStationOne();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        primeStationOne.setMegaEmail(preferences.getString(getString(R.string.pref_key_mega_login_email),
                getString(R.string.pref_default_mega_login_email)));
        primeStationOne.setMegaPassword(preferences.getString(getString(R.string.pref_key_mega_login_password),
                getString(R.string.pref_default_mega_login_password)));

        new MaterialDialog.Builder(getActivity())
                .title("Login to MEGA")
                .content("Enter login email for Mega.co.nz...")
                .positiveText("NEXT >")
                .negativeText("CANCEL")
                .input("login email", primeStationOne.getMegaEmail(), false, (materialDialog, charSequence) -> {
                    Timber.d("email input: " + charSequence);
                    primeStationOne.setMegaEmail(charSequence.toString());
                    new MaterialDialog.Builder(getActivity())
                            .title("Login to MEGA")
                            .content("Enter login information for Mega.co.nz...")
                            .positiveText("LOGIN >")
                            .negativeText("CANCEL")
                            .input("login password", primeStationOne.getMegaPassword(), false, (materialDialog1, charSequence1) -> {
                                Timber.d("password input: " + charSequence1);
                                primeStationOne.setMegaPassword(charSequence1.toString());
                                primeStationOne.updateStoredPrimestation(getActivity());
                                sendCommandToCurrentPrimeStationOne("echo \"Creating your .megarc file from provided email $email and not printing your password out of courtesy, you are welcome...\"\n" +
                                        "cat > /home/pi/.megarc << _EOF_\n" +
                                        "[Login]\n" +
                                        "Username = " + primeStationOne.getMegaEmail() + "\n" +
                                        "Password = " + primeStationOne.getMegaPassword() + "\n" +
                                        "_EOF_\n" +
                                        "cat ~/.megarc ; megals", true, mTvStatus);
                            })
                            .show();
                })
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_primestation_one_cloud_backup_controls, container, false);
        ButterKnife.bind(this, rootView);
        initializeCommander();
        return rootView;
    }
}
