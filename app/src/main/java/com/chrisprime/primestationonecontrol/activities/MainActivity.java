package com.chrisprime.primestationonecontrol.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.fragments.NavigationDrawerFragment;
import com.chrisprime.primestationonecontrol.fragments.PrimeStationOneDiscoveryFragment;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        //TODO: Delete once mastered:
        rxJavaHelloWorldVerbose();
        rxJavaHelloWorldShorthand();
        rxJavaHelloWorldHash();
        rxJavaHelloWorldDoubleTransformedHash();
    }

    private void rxJavaHelloWorldVerbose() {
        //RxJava experiments:
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world verbose!");
                        sub.onCompleted();
                    }
                }
        ).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s + " -Love, Chris";
            }
        });

        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Timber.d(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        myObservable.subscribe(mySubscriber);
        mySubscriber.unsubscribe();
    }

    private void rxJavaHelloWorldShorthand() {
        //RxJava experiments:
        Observable.just("Hello, world shorthand!")
                .map(s -> s + " -Love, Chris")
                .subscribe(s -> {
                    Timber.d(s);
                });
    }

    private void rxJavaHelloWorldHash() {
        //RxJava experiments:
        Observable.just("Hello, world hash!")
                .map(String::hashCode)
                .subscribe(i -> {
                    Timber.d("Hello, world hash = " + i);
                });
    }

    private void rxJavaHelloWorldDoubleTransformedHash() {
        //RxJava experiments:
        Observable.just("Hello, world hash!")
                .map(String::hashCode)
                .map(i -> Integer.toString(i))
                .subscribe(s -> {
                    Timber.d("Hello, world double-transformed hash = " + s);
                });
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        mTitle = getString(R.string.title_primestation_search);
        fragmentManager.beginTransaction()
                .replace(R.id.container, PrimeStationOneDiscoveryFragment.newInstance())
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
        }

        return super.onOptionsItemSelected(item);
    }
}
