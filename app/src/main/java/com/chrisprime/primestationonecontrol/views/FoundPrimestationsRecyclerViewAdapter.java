package com.chrisprime.primestationonecontrol.views;

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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cpaian on 7/18/15.
 */

public class FoundPrimestationsRecyclerViewAdapter extends RecyclerView.Adapter<FoundPrimestationsRecyclerViewAdapter.FoundPrimeStationsRecyclerViewHolder> {
    private List<PrimeStationOne> mPrimeStationOneList;

    public FoundPrimestationsRecyclerViewAdapter(List<PrimeStationOne> primeStationOneList) {
        this.mPrimeStationOneList = primeStationOneList;
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

        //Setting text view title
        foundPrimeStationsRecyclerViewHolder.textView.setText(primeStationOne.getIpAddress() +
                "\n" + primeStationOne.getHostname() + "\n" + primeStationOne.getVersion() + "\n" +
                primeStationOne.getPiUser() + ":" + primeStationOne.getPiPassword() + "\n" +
                primeStationOne.getMac());
        ;
    }

    @Override
    public int getItemCount() {
        return (null != mPrimeStationOneList ? mPrimeStationOneList.size() : 0);
    }

    @Override
    public long getItemId(int position) {return position;}

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
            FileUtilities.storeCurrentPrimeStationToJson(v.getContext(), primeStationOne);
        }
    }
}
