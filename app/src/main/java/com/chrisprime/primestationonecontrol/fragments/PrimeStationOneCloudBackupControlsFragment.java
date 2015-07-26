package com.chrisprime.primestationonecontrol.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chrisprime.primestationonecontrol.R;

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

    @OnClick(R.id.button_login_to_mega)
    void onMegaLoginButtonClicked(View view) {
        Timber.d("Mega login button clicked!");

        //TODO: Pop up dialog box allowing enter of username and password, prepopulated with preference (settable in settings), and stored as preference after press OK
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        CharSequence storedLoginEmail = preferences.getString(getString(R.string.pref_key_mega_login_email),
                getString(R.string.pref_default_mega_login_email));
        CharSequence storedLoginPassword = preferences.getString(getString(R.string.pref_key_mega_login_password),
                getString(R.string.pref_default_mega_login_password));
        new MaterialDialog.Builder(getActivity())
                .title("Login to MEGA")
                .content("Enter login information for Mega.co.nz...")
                .positiveText("LOGIN")
                .negativeText("CANCEL")
                .input("login email", storedLoginEmail, false, (materialDialog, charSequence) -> {
                    Timber.d("email input: " + charSequence);
                })
                .input("login password", storedLoginPassword, false, (materialDialog, charSequence) -> {
                    Timber.d("password input: " + charSequence);
                })
                .show();
        sendCommandToCurrentPrimeStationOne("create_megarc_login_file \"" + storedLoginEmail + "\" \"" + storedLoginPassword + "\"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_primestation_one_cloud_backup_controls, container, false);
        ButterKnife.bind(this, rootView);
        initializeButtonList();
        return rootView;
    }
}
