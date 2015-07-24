package com.chrisprime.primestationonecontrol.views;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.NetworkUtilities;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by cpaian on 7/18/15.
 */

public class FoundPrimestationsRecyclerViewAdapter extends RecyclerView.Adapter<FoundPrimestationsRecyclerViewAdapter.FoundPrimeStationsRecyclerViewHolder> {
    private List<PrimeStationOne> mPrimeStationOneList;
    private Context mContext;

    public interface ShowImage {
        void show(Uri uri);
    }

    private ShowImage mShowImage;

    public FoundPrimestationsRecyclerViewAdapter(Context context, List<PrimeStationOne> primeStationOneList, ShowImage showImage) {
        this.mPrimeStationOneList = primeStationOneList;
        this.mContext = context;
        this.mShowImage = showImage;
    }

    @Override
    public FoundPrimeStationsRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_found_primestations_item, viewGroup, false);
        return new FoundPrimeStationsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoundPrimeStationsRecyclerViewHolder foundPrimeStationsRecyclerViewHolder, int i) {
        PrimeStationOne primeStationOne = mPrimeStationOneList.get(i);
        foundPrimeStationsRecyclerViewHolder.primeStationOne = primeStationOne;

        //Download image using picasso library
        Picasso.with(mContext).load(PrimeStationOne.PRIMESTATION_IMGUR_SPLASHSCREEN_SOURCE_IMAGE_URL)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .placeholder(R.drawable.ic_launcher)
                .into(foundPrimeStationsRecyclerViewHolder.imageView);

        //Setting text view title
        foundPrimeStationsRecyclerViewHolder.textView.setText(primeStationOne.getIpAddress() +
                "\n" + primeStationOne.getHostname() + "\n" + primeStationOne.getVersion() + "\n" + primeStationOne.getMac());
    }

    @Override
    public int getItemCount() {
        return (null != mPrimeStationOneList ? mPrimeStationOneList.size() : 0);
    }

    public class FoundPrimeStationsRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.thumbnail)
        ImageView imageView;
        @Bind(R.id.title)
        TextView textView;
        @Bind(R.id.progress)
        ProgressBar progressBar;

        PrimeStationOne primeStationOne;

        public FoundPrimeStationsRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (primeStationOne.isRetrievedSplashscreen()) {    //Already retrieved this splashscreen
                //Display full screen for quick reference
                if (mShowImage != null) {
                    mShowImage.show(primeStationOne.getSplashscreenUri());
                }
            } else {
                itemView.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                Observable retrieveImageObservable = Observable.create(
                        sub -> {
                            sub.onNext(
                                    NetworkUtilities.sshRetrieveAndSavePrimeStationFile(v.getContext(), primeStationOne.getIpAddress(),
                                            PrimeStationOne.DEFAULT_PI_USERNAME, PrimeStationOne.DEFAULT_PI_PASSWORD, PrimeStationOne.DEFAULT_PI_SSH_PORT, PrimeStationOne.DEFAULT_PRIMESTATION_SPLASH_SCREEN_FILE_LOCATION, PrimeStationOne.SPLASHSCREENWITHCONTROLSANDVERSION_PNG_FILE_NAME));
                            sub.onCompleted();
                        }
                )
//                .map(s -> s + " -Love, Chris")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

                Subscriber<Uri> retrieveImageSubscriber = new Subscriber<Uri>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("retrieval of image completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error with subscriber: " + e + ": " + e.getMessage());
                    }

                    @Override
                    public void onNext(Uri uri) {
                        Picasso.with(v.getContext()).load(uri)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                        itemView.setClickable(true);
                                        Timber.d("Successfully loaded fullscreen image from uri: " + uri);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                        itemView.setClickable(true);
                                        Timber.e("Failed to load fullscreen image from uri: " + uri);
                                    }
                                });
                        primeStationOne.setSplashscreenUri(uri);
                        primeStationOne.setRetrievedSplashscreen(true);
                    }
                };
                Subscription retreiveImageSubscription = retrieveImageObservable.subscribe(retrieveImageSubscriber);

                Toast.makeText(v.getContext(), "Item no. " + getAdapterPosition() + ": "
                        + primeStationOne.getIpAddress() + " onClick!  Loading its splashscreen into the imageView!", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
