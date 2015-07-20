package com.chrisprime.primestationonecontrol.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisprime.primestationonecontrol.R;
import com.chrisprime.primestationonecontrol.model.PrimeStationOne;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by cpaian on 7/18/15.
 */

public class FoundPrimestationsRecyclerViewAdapter extends RecyclerView.Adapter<FoundPrimestationsRecyclerViewAdapter.FoundPrimeStationsRecyclerViewHolder> {
    public static final String PRIMESTATION_IMGUR_SPLASHSCREEN_SOURCE_IMAGE_URL = "http://i.imgur.com/UnMdAZX.png";
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
        Picasso.with(mContext).load(PRIMESTATION_IMGUR_SPLASHSCREEN_SOURCE_IMAGE_URL)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .placeholder(R.drawable.ic_launcher)
                .into(foundPrimeStationsRecyclerViewHolder.imageView);

        //Setting text view title
        foundPrimeStationsRecyclerViewHolder.textView.setText(primeStationOne.getIpAddress() +
                "\n" + primeStationOne.getHostname() + "\n" + primeStationOne.getVersion());
    }

    @Override
    public int getItemCount() {
        return (null != mPrimeStationOneList ? mPrimeStationOneList.size() : 0);
    }

    public class FoundPrimeStationsRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView textView;
        protected PrimeStationOne primeStationOne;

        public FoundPrimeStationsRecyclerViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Item no. " + getAdapterPosition() + ": "
                    + primeStationOne.getIpAddress() + " onClick!", Toast.LENGTH_SHORT).show();
        }
    }
}
