package com.example.cyril.mobilespeedometer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cyril.mobilespeedometer.model.Result
import com.example.cyril.mobilespeedometer.R

class RecViewResultsAdapter (private val results: List<Result>): RecyclerView.Adapter<RecViewResultsAdapter.RecViewHolder>() {

    class RecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView = itemView?.findViewById(R.id.item_textView) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewResultsAdapter.RecViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return RecViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecViewHolder, position: Int) {
        val date = results[position].date.toString()
        val time = results[position].time.toString()
        val result = results[position].resultTime.toString()
        holder?.textView?.text = "$date; $time; $result"
    }

    override fun getItemCount(): Int {
        return results.size
    }
}