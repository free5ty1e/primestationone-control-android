package com.chrisprime.primestationonecontrol.fragments

import android.widget.*
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities
import com.chrisprime.primestationonecontrol.utilities.SshCommandConsoleStdOutLineListener
import com.chrisprime.primestationonecontrol.utilities.TextViewUtilities
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import java.util.*

abstract class PrimeStationOneBaseSshCommanderFragment : BaseFragment() {

    protected var mPrimeStationCommandObservable: Observable<Int>? = null
    protected var mPrimeStationCommandSubscriber: Subscriber<Int>? = null
    protected var mPrimeStationCommandSubscription: Subscription? = null
    protected var mButtonList: MutableList<Button> = ArrayList()

    protected fun sendCommandToCurrentPrimeStationOne(command: String, waitForReturnValueAndCommandOutput: Boolean,
                                                      textViewForConsoleUpdates: TextView?, textViewForStatus: TextView,
                                                      scrollViewForStatus: ScrollView) {
        val currentPrimeStationOne = PrimeStationOneControlApplication.instance.currentPrimeStationOne
        if (currentPrimeStationOne == null) {
            Toast.makeText(activity, "No Primestation currently selected, please select one from Search n Scan screen...", Toast.LENGTH_SHORT).show()
        } else {
            textViewForStatus.text = "Sending command to current PrimeStation One at " + currentPrimeStationOne.ipAddress + ": " + command
            setAllButtonsEnabledInList(false)
            mPrimeStationCommandObservable = Observable.create(
                    Observable.OnSubscribe<kotlin.Int> { sub ->
                        sub.onNext(NetworkUtilities.sendSshCommandToPi(currentPrimeStationOne.ipAddress!!,
                                currentPrimeStationOne.piUser!!,
                                currentPrimeStationOne.piPassword!!,
                                PrimeStationOne.DEFAULT_PI_SSH_PORT, command, waitForReturnValueAndCommandOutput,
                                sshCommandConsoleStdOutLineListener = object: SshCommandConsoleStdOutLineListener {
                                    override fun processConsoleStdOutLine(line: String) {
                                        val processedLine = processSshConsoleStdOutLine(line)
                                        val activity = activity
                                        if (textViewForConsoleUpdates != null && activity != null) {
                                            activity.runOnUiThread {
                                                TextViewUtilities.addLinesToTextView(processedLine,
                                                        textViewForConsoleUpdates, textViewForConsoleUpdates.parent as ScrollView)
                                            }
                                        }
                                    }
                                }))
                        sub.onCompleted()
                    }).subscribeOn(Schedulers.io())//                .map(s -> s + " -Love, Chris")
                    .observeOn(AndroidSchedulers.mainThread())

            mPrimeStationCommandSubscriber = object : Subscriber<Int>() {

                override fun onNext(i: Int?) {
                    Timber.d(".onNext(command exit code: $i)")
                }

                override fun onCompleted() {
                    //                findPiButton.setEnabled(true);
                    activity.runOnUiThread {
                        TextViewUtilities.addLinesToTextView("\nCommand sent to current PrimeStation One at " + currentPrimeStationOne.ipAddress,
                                textViewForStatus, scrollViewForStatus)
                        setAllButtonsEnabledInList(true)
                    }
                    unsubscribe()
                }

                override fun onError(e: Throwable) {
                    Timber.e(e, "Error with subscriber: " + e + ": " + e.message)
                }
            }
            mPrimeStationCommandSubscription = mPrimeStationCommandObservable!!.subscribe(mPrimeStationCommandSubscriber)
        }
    }

    /**
     * Override me to apply additional logic to any incoming SSH console stdout lines as they arrive, before they reach the terminal
     * @param line
     * *
     * @return
     */
    protected fun processSshConsoleStdOutLine(line: String): String {
        return line
    }

    protected fun determineIsCommanderBusy(): Boolean {
        return mPrimeStationCommandSubscription != null && !mPrimeStationCommandSubscriber!!.isUnsubscribed
    }

    /**
     * Make sure to call this in your fragment's onCreateView (or onViewCreated if you are using Kotlin synthetics)
     */
    protected fun initializeCommander(buttonContainer: LinearLayout) {
        //Populate the button list so we can easily run through and enable or disable them
        for (i in 0..buttonContainer.childCount - 1) {
            mButtonList.add(buttonContainer.getChildAt(i) as Button)
        }
    }

    protected fun setAllButtonsEnabledInList(enabled: Boolean) {
        for (button in mButtonList) {
            button.isEnabled = enabled
        }
    }
}
