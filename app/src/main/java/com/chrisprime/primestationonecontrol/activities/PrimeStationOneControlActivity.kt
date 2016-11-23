package com.chrisprime.primestationonecontrol.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.dagger.Injector
import com.chrisprime.primestationonecontrol.fragments.NavigationDrawerFragment
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneCloudBackupControlsFragment
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneDiscoveryFragment
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneGeneralControlsFragment
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.chrisprime.primestationonecontrol.utilities.FileUtilities
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities
import com.chrisprime.primestationonecontrol.views.WebViewFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class PrimeStationOneControlActivity : BaseEventBusAppCompatActivity(), NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private var mNavigationDrawerFragment: NavigationDrawerFragment? = null

    /**
     * Used to store the last screen title. For use in [.restoreActionBar].
     */
    private var mTitle: CharSequence? = null

    private var mRetrieveImageObservable: Observable<Uri>? = null

    override fun onStart() {
        super.onStart()
        mIoThreadPoolEnabled = true
    }

    override fun onStop() {
        super.onStop()
        synchronized(mIoThreadPoolSync) {
            mIoThreadPoolEnabled = false
        }
    }

    private var mRetrieveImageSubscriber: Subscriber<Uri>? = null
    
    private var mRetreiveImageSubscription: Subscription? = null

    @Inject
    lateinit var mIoThreadPool: ExecutorService

    private val mIoThreadPoolSync = Any()
    private var mIoThreadPoolEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Injector.applicationComponent.inject(this)

        iv_fullscreen!!.setOnClickListener { v ->
            iv_fullscreen!!.visibility = View.GONE
            pb_centered!!.visibility = View.GONE
        }

        //Uncomment if we run out of ring buffer
        //System.setProperty("rx.ring-buffer.size", "128");

        mNavigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer) as NavigationDrawerFragment
        mTitle = title

        // Set up the drawer.
        val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mNavigationDrawerFragment!!.setUp(
                R.id.navigation_drawer,
                drawerLayout)
        setupHamburgerMenuUpButtonToggleAnimation(drawerLayout)
    }

    private fun setupHamburgerMenuUpButtonToggleAnimation(drawerLayout: DrawerLayout) {
        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */) {
            override fun onDrawerClosed(view: View?) {
                super.onDrawerClosed(view)
                if (Build.VERSION.SDK_INT >= 11) {
                    invalidateOptionsMenu()
                } else {
                    supportInvalidateOptionsMenu()
                }
                syncState()
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                if (Build.VERSION.SDK_INT >= 11) {
                    invalidateOptionsMenu()
                } else {
                    supportInvalidateOptionsMenu()
                }
                syncState()
            }
        }
        drawerLayout.setDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    val preferences: SharedPreferences
        get() = getDefaultSharedPreferences(this)

    fun getPreferenceBoolean(@StringRes prefKeyRes: Int, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(getString(prefKeyRes), defaultValue)
    }

    fun getPreferenceString(@StringRes prefKeyRes: Int): String {
        return preferences.getString(getString(prefKeyRes), null)
    }

    fun getPreferenceListSelectedValue(@StringRes prefKeyRes: Int): String {
        return preferences.getString(getString(prefKeyRes), "-1")
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        // update the main content by replacing fragments


        when (position) {
            NAVIGATION_INDEX_DISCOVERY -> newMainFragment(PrimeStationOneDiscoveryFragment.newInstance(), R.string.title_primestation_search)
            NAVIGATION_INDEX_GENERAL_CONTROLS -> newMainFragment(PrimeStationOneGeneralControlsFragment.newInstance(), R.string.title_primestation_general_controls)
            NAVIGATION_INDEX_CLOUD_BACKUP -> newMainFragment(PrimeStationOneCloudBackupControlsFragment.newInstance(), R.string.title_primestation_cloud_backup_controls)
            NAVIGATION_INDEX_VIRTUAL_GAMEPAD -> newMainFragment(WebViewFragment.newInstance(getString(R.string.title_primestation_virtual_gamepad), "http://" + getCurrentPrimeStationOne()!!.ipAddress + ":8080"), R.string.title_primestation_virtual_gamepad)
            NAVIGATION_INDEX_SETTINGS -> startActivity(Intent(this, SettingsActivity::class.java))
            else -> {
            }
        }
    }

    fun getCurrentPrimeStationOne(): PrimeStationOne? {
        return (application as PrimeStationOneControlApplication).currentPrimeStationOne
    }

    fun newMainFragment(fragment: Fragment, titleResourceId: Int) {
        val fragmentManager = supportFragmentManager
        mTitle = getString(titleResourceId)
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    fun restoreActionBar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.title = mTitle
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!mNavigationDrawerFragment!!.isDrawerOpen) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            menuInflater.inflate(R.menu.main, menu)
            restoreActionBar()
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        } else if (id == R.id.action_show_quickref) {
            val primeStationOne = PrimeStationOneControlApplication.instance.currentPrimeStationOne
            if (primeStationOne == null) {
                Toast.makeText(this@PrimeStationOneControlActivity, "No PrimeStation One currently selected!", Toast.LENGTH_SHORT).show()
            } else {

                if (determineIfCurrentlyDownloadingSplashscreen()) {
                    Toast.makeText(this, "Currently downloading, please wait...", Toast.LENGTH_SHORT).show()
                } else {
                    val currentPrimestationReportText = "Current PrimeStation One is: " + primeStationOne.toString()
                    if (primeStationOne.isRetrievedSplashscreen!!) {    //Already retrieved this splashscreen
                        Toast.makeText(this, "Displaying splashscreen!  " + currentPrimestationReportText, Toast.LENGTH_LONG).show()
                        displayFullScreenQuickRef(primeStationOne)
                    } else {
                        Toast.makeText(this, "Retrieving splashscreen...  " + currentPrimestationReportText, Toast.LENGTH_LONG).show()

                        pb_centered!!.visibility = View.VISIBLE
                        mRetrieveImageObservable = Observable.create<Uri> { sub ->
                            sub.onNext(
                                    NetworkUtilities.sshRetrieveAndSavePrimeStationFile(this, primeStationOne.ipAddress!!,
                                            primeStationOne.piUser!!, primeStationOne.piPassword!!,
                                            PrimeStationOne.DEFAULT_PI_SSH_PORT, PrimeStationOne.DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION,
                                            PrimeStationOne.SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME))
                            sub.onCompleted()
                        }.subscribeOn(Schedulers.io())//                .map(s -> s + " -Love, Chris")
                                .observeOn(AndroidSchedulers.mainThread())

                        mRetrieveImageSubscriber = object : Subscriber<Uri>() {
                            override fun onCompleted() {
                                val message = "retrieval of image completed!"
                                Timber.d(message)
                                Toast.makeText(this@PrimeStationOneControlActivity, message, Toast.LENGTH_SHORT).show()
                                pb_centered!!.visibility = View.GONE
                                displayFullScreenQuickRef(primeStationOne)
                                FileUtilities.storeCurrentPrimeStationToJson(this@PrimeStationOneControlActivity, primeStationOne)
                            }

                            override fun onError(e: Throwable) {
                                Timber.e(e, "Error with subscriber: " + e + ": " + e.message)
                                pb_centered!!.visibility = View.GONE
                            }

                            override fun onNext(uri: Uri?) {
                                if (uri == null) {
                                    Toast.makeText(this@PrimeStationOneControlActivity, "Error downloading image from Primestation, maybe try again?", Toast.LENGTH_SHORT).show()
                                } else {
                                    primeStationOne.splashscreenUriString = uri.toString()
                                    primeStationOne.isRetrievedSplashscreen = true
                                }
                            }
                        }
                        mRetreiveImageSubscription = mRetrieveImageObservable!!.subscribe(mRetrieveImageSubscriber!!)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayFullScreenQuickRef(primeStationOne: PrimeStationOne) {
        pb_centered!!.visibility = View.VISIBLE

        //Display full screen for quick reference
        val splashscreenUri: Uri = Uri.parse(primeStationOne.splashscreenUriString)
        Picasso.with(this).load(splashscreenUri).rotate(90f)//TODO: Look into why fit() breaks gingerbread's ability to show the fullscreen image
                //                .fit()
                .into(iv_fullscreen!!, object : Callback {
                    override fun onSuccess() {
                        iv_fullscreen!!.visibility = View.VISIBLE
                        pb_centered!!.visibility = View.GONE
                    }

                    override fun onError() {
                        Toast.makeText(this@PrimeStationOneControlActivity, "Error loading " + splashscreenUri, Toast.LENGTH_LONG).show()
                        pb_centered!!.visibility = View.GONE
                    }
                })
    }

    private fun determineIfCurrentlyDownloadingSplashscreen(): Boolean {
        return mRetreiveImageSubscription != null && !mRetrieveImageSubscriber!!.isUnsubscribed
    }

    fun runOnIoThread(runnable: () -> Unit) {
        synchronized(mIoThreadPoolSync) {
            if (mIoThreadPoolEnabled) {
                mIoThreadPool!!.submit(runnable)
            }
        }
    }

    companion object {

        val NAVIGATION_INDEX_DISCOVERY = 0
        val NAVIGATION_INDEX_GENERAL_CONTROLS = 1
        val NAVIGATION_INDEX_CLOUD_BACKUP = 2
        val NAVIGATION_INDEX_VIRTUAL_GAMEPAD = 3
        val NAVIGATION_INDEX_SETTINGS = 4 //Settings -- keep moving this one so it's at the bottom, will have to re-enumerate if more screens added!
    }

}
