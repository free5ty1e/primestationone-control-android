package com.chrisprime.primestationonecontrol.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.chrisprime.primestationonecontrol.PrimeStationOneControlApplication
import com.chrisprime.primestationonecontrol.R
import com.chrisprime.primestationonecontrol.model.PrimeStationOne
import com.chrisprime.primestationonecontrol.utilities.FileUtilities
import kotlinx.android.synthetic.main.recyclerview_found_primestations_item.view.*


/**
 * Created by cpaian on 7/18/15.
 */

class FoundPrimestationsRecyclerViewAdapter(private val mPrimeStationOneList: List<PrimeStationOne>?) : RecyclerView.Adapter<FoundPrimestationsRecyclerViewAdapter.FoundPrimeStationsRecyclerViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FoundPrimeStationsRecyclerViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recyclerview_found_primestations_item, viewGroup, false)
        return FoundPrimeStationsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(foundPrimeStationsRecyclerViewHolder: FoundPrimeStationsRecyclerViewHolder, i: Int) {
        val primeStationOne = mPrimeStationOneList!![i]
        foundPrimeStationsRecyclerViewHolder.primeStationOne = primeStationOne

        //Setting text view title

        foundPrimeStationsRecyclerViewHolder.itemView.found_primestation_title.text = primeStationOne.ipAddress +
                "\n" + primeStationOne.hostname + "\n" + primeStationOne.version + "\n" +
                primeStationOne.piUser + ":" + primeStationOne.piPassword + "\n" +
                primeStationOne.mac
    }

    override fun getItemCount(): Int {
        return if (null != mPrimeStationOneList) mPrimeStationOneList.size else 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class FoundPrimeStationsRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var primeStationOne: PrimeStationOne? = null

        init {
            itemView.isClickable = true
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            PrimeStationOneControlApplication.instance.currentPrimeStationOne = primeStationOne
            Toast.makeText(v.context, "Current PrimeStation One set to: " + primeStationOne!!.toString(), Toast.LENGTH_LONG).show()
            FileUtilities.storeCurrentPrimeStationToJson(v.context, primeStationOne!!)
        }
    }
}
