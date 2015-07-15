package com.chrisprime.primestationonecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


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
                Log.d(LOG_TAG, s);
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
                    Log.d(LOG_TAG, s);
                });
    }

    private void rxJavaHelloWorldHash() {
        //RxJava experiments:
        Observable.just("Hello, world hash!")
                .map(String::hashCode)
                .subscribe(i -> {
                    Log.d(LOG_TAG, "Hello, world hash = " + i);
                });
    }


    private void setupHamburgerMenuUpButtonToggleAnimation(final DrawerLayout drawerLayout) {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.actionbar_up, R.string.actionbar_menu) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
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
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button findPiButton = (Button) rootView.findViewById(R.id.button_find_pi);
            Observable<OnClickEvent> buttonObservable = ViewObservable.clicks(findPiButton, false);
            Subscription findPiButtonSubscription = buttonObservable.subscribe(new Action1<OnClickEvent>() {
                @Override
                public void call(OnClickEvent onClickEvent) {
                    Log.d(LOG_TAG, "findPi button clicked!");

                    Observable<String> findPiObservable = Observable.create(
                            new Observable.OnSubscribe<String>() {
                                @Override
                                public void call(Subscriber<? super String> sub) {
                                    sub.onNext(findPi());
                                    sub.onCompleted();
                                }
                            }
                    ).map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            return s + " -Love, Chris";
                        }
                    });
//                    findPiObservable.subscribeOn(AndroidSchedulers.handlerThread(new Handler()));
                    findPiObservable.subscribeOn(Schedulers.io());

                    Subscriber<String> findPiSubscriber = new Subscriber<String>() {
                        @Override
                        public void onNext(String s) {
                            Log.d(getClass().getSimpleName(), s);
                        }

                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    };

                    findPiObservable.subscribe(findPiSubscriber);
                    findPiSubscriber.unsubscribe();
                }

            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private static String findPi() {
        String user = "pi";
        String password = "raspberry";
        String host = "192.168.1.53";
        int port = 22;
        String foundPi = "";

        String remoteFile = "/home/pi/primestationone/reference/version.txt";

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            Log.d(LOG_TAG, "Establishing Connection...");
            session.connect();
            Log.d(LOG_TAG, "Connection established.");
            Log.d(LOG_TAG, "Crating SFTP Channel.");
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            Log.d(LOG_TAG, "SFTP Channel created.");


            InputStream out = null;
            out = sftpChannel.get(remoteFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(out));
            String line;
            while ((line = br.readLine()) != null) {
                foundPi += line + "\n";
                Log.d(LOG_TAG, line);
            }
            br.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return foundPi;
    }

}
