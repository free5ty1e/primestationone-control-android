package com.chrisprime.primestationonecontrol.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.dagger.Injector;
import com.chrisprime.primestationonecontrol.fragments.NavigationDrawerFragment;
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneCloudBackupControlsFragment;
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneDiscoveryFragment;
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneGeneralControlsFragment;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.FileUtilities;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;

public class PrimeStationOneControlActivity extends BaseEventBusAppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int NAVIGATION_INDEX_DISCOVERY = 0;
    public static final int NAVIGATION_INDEX_GENERAL_CONTROLS = 1;
    public static final int NAVIGATION_INDEX_CLOUD_BACKUP = 2;
    public static final int NAVIGATION_INDEX_SETTINGS = 3; //Settings -- keep moving this one so it's at the bottom, will have to re-enumerate if more screens added!
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Observable mRetrieveImageObservable;

    @Override
    protected void onStart() {
        super.onStart();
        mIoThreadPoolEnabled = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        synchronized (mIoThreadPoolSync) {
            mIoThreadPoolEnabled = false;
        }
    }

    private Subscriber<Uri> mRetrieveImageSubscriber;

    @Bind(R.id.iv_fullscreen)
    ImageView mFullScreenImageView;

    @Bind(R.id.pb_centered)
    ProgressBar mCenteredProgressSpinner;
    private Subscription mRetreiveImageSubscription;

    @Inject
    ExecutorService mIoThreadPool;

    private final Object mIoThreadPoolSync = new Object();
    private boolean mIoThreadPoolEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Injector.getApplicationComponent().inject(this);

        mFullScreenImageView.setOnClickListener(v -> {
            mFullScreenImageView.setVisibility(View.GONE);
            mCenteredProgressSpinner.setVisibility(View.GONE);
        });

        //Uncomment if we run out of ring buffer
        //System.setProperty("rx.ring-buffer.size", "128");

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);
        setupHamburgerMenuUpButtonToggleAnimation(drawerLayout);
    }

    private void setupHamburgerMenuUpButtonToggleAnimation(final DrawerLayout drawerLayout) {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (Build.VERSION.SDK_INT >= 11) {
                    invalidateOptionsMenu();
                } else {
                    supportInvalidateOptionsMenu();
                }
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (Build.VERSION.SDK_INT >= 11) {
                    invalidateOptionsMenu();
                } else {
                    supportInvalidateOptionsMenu();
                }
                syncState();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public SharedPreferences getPreferences() {
        return getDefaultSharedPreferences(this);
    }

    public boolean getPreferenceBoolean(@StringRes int prefKeyRes, boolean defaultValue) {
        return getPreferences().getBoolean(getString(prefKeyRes), defaultValue);
    }

    public String getPreferenceString(@StringRes int prefKeyRes) {
        return getPreferences().getString(getString(prefKeyRes), null);
    }

    public String getPreferenceListSelectedValue(@StringRes int prefKeyRes) {
        return getPreferences().getString(getString(prefKeyRes),"-1");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        switch (position) {
            case NAVIGATION_INDEX_DISCOVERY:
                newMainFragment(PrimeStationOneDiscoveryFragment.newInstance(), R.string.title_primestation_search);
                break;
            case NAVIGATION_INDEX_GENERAL_CONTROLS:
                newMainFragment(PrimeStationOneGeneralControlsFragment.newInstance(), R.string.title_primestation_general_controls);
                break;
            case NAVIGATION_INDEX_CLOUD_BACKUP:
                newMainFragment(PrimeStationOneCloudBackupControlsFragment.newInstance(), R.string.title_primestation_cloud_backup_controls);
                break;
            case NAVIGATION_INDEX_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }
    }

    public void newMainFragment(Fragment fragment, int titleResourceId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mTitle = getString(titleResourceId);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_show_quickref) {
            PrimeStationOne primeStationOne = PrimeStationOneControlApplication.instance.getCurrentPrimeStationOne();
            if (primeStationOne == null) {
                Toast.makeText(PrimeStationOneControlActivity.this, "No PrimeStation One currently selected!", Toast.LENGTH_SHORT).show();
            } else {

                if (determineIfCurrentlyDownloadingSplashscreen()) {
                    Toast.makeText(this, "Currently downloading, please wait...", Toast.LENGTH_SHORT).show();
                } else {
                    String currentPrimestationReportText = "Current PrimeStation One is: " + primeStationOne.toString();
                    if (primeStationOne.isRetrievedSplashscreen()) {    //Already retrieved this splashscreen
                        Toast.makeText(this, "Displaying splashscreen!  " + currentPrimestationReportText, Toast.LENGTH_LONG).show();
                        displayFullScreenQuickRef(primeStationOne);
                    } else {
                        Toast.makeText(this, "Retrieving splashscreen...  " + currentPrimestationReportText, Toast.LENGTH_LONG).show();

                        mCenteredProgressSpinner.setVisibility(View.VISIBLE);
                        mRetrieveImageObservable = Observable.create(
                                sub -> {
                                    sub.onNext(
                                            NetworkUtilities.sshRetrieveAndSavePrimeStationFile(this, primeStationOne.getIpAddress(),
                                                    primeStationOne.getPiUser(), primeStationOne.getPiPassword(),
                                                    PrimeStationOne.DEFAULT_PI_SSH_PORT, PrimeStationOne.DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION,
                                                    PrimeStationOne.SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME));
                                    sub.onCompleted();
                                }
                        )
//                .map(s -> s + " -Love, Chris")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                        mRetrieveImageSubscriber = new Subscriber<Uri>() {
                            @Override
                            public void onCompleted() {
                                String message = "retrieval of image completed!";
                                Timber.d(message);
                                Toast.makeText(PrimeStationOneControlActivity.this, message, Toast.LENGTH_SHORT).show();
                                mCenteredProgressSpinner.setVisibility(View.GONE);
                                displayFullScreenQuickRef(primeStationOne);
                                FileUtilities.storeCurrentPrimeStationToJson(PrimeStationOneControlActivity.this, primeStationOne);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "Error with subscriber: " + e + ": " + e.getMessage());
                                mCenteredProgressSpinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(Uri uri) {
                                if (uri == null) {
                                    Toast.makeText(PrimeStationOneControlActivity.this, "Error downloading image from Primestation, maybe try again?", Toast.LENGTH_SHORT).show();
                                } else {
                                    primeStationOne.setSplashscreenUri(uri);
                                    primeStationOne.setRetrievedSplashscreen(true);
                                }
                            }
                        };
                        mRetreiveImageSubscription = mRetrieveImageObservable.subscribe(mRetrieveImageSubscriber);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFullScreenQuickRef(PrimeStationOne primeStationOne) {
        mCenteredProgressSpinner.setVisibility(View.VISIBLE);

        //Display full screen for quick reference
        Uri splashscreenUri = primeStationOne.getSplashscreenUri();
        Picasso.with(this)
                .load(splashscreenUri)

//TODO: Look into why fit() breaks gingerbread's ability to show the fullscreen image
//                .fit()

                .rotate(90)
                .into(mFullScreenImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mFullScreenImageView.setVisibility(View.VISIBLE);
                        mCenteredProgressSpinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(PrimeStationOneControlActivity.this, "Error loading "
                                + splashscreenUri, Toast.LENGTH_LONG).show();
                        mCenteredProgressSpinner.setVisibility(View.GONE);
                    }
                });
    }

    private boolean determineIfCurrentlyDownloadingSplashscreen() {
        return mRetreiveImageSubscription != null && !mRetrieveImageSubscriber.isUnsubscribed();
    }

    public void runOnIoThread(@NonNull Runnable runnable) {
        synchronized (mIoThreadPoolSync) {
            if (mIoThreadPoolEnabled) {
                mIoThreadPool.submit(runnable);
            }
        }
    }

}
