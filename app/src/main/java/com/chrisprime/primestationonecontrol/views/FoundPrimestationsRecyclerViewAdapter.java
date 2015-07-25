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

import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication;
import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.chrisprime.primestationonecontrol.utilities.FileUtilities;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by cpaian on 7/18/15.
 */

public class FoundPrimestationsRecyclerViewAdapter extends RecyclerView.Adapter<FoundPrimestationsRecyclerViewAdapter.FoundPrimeStationsRecyclerViewHolder> {
    private List<PrimeStationOne> mPrimeStationOneList;
    private Context mContext;

    public FoundPrimestationsRecyclerViewAdapter(Context context, List<PrimeStationOne> primeStationOneList) {
        this.mPrimeStationOneList = primeStationOneList;
        this.mContext = context;
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
            PrimeStationOneControlApplication.getInstance().setCurrentPrimeStationOne(primeStationOne);
            Toast.makeText(v.getContext(), "Current PrimeStation One set to: " + primeStationOne.toString(), Toast.LENGTH_LONG).show();

            //Store current primestation as JSON file
            String jsonString = new Gson().toJson(primeStationOne);
            Timber.d("bundled current primestation into JSON string:\n" + jsonString);
            FileUtilities.createAndSaveFile(v.getContext(), PrimeStationOne.CURRENT_PRIMESTATION_JSON_FILENAME, jsonString);
        }
    }
}
