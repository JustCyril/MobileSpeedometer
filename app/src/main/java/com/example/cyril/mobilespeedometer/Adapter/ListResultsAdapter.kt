package com.example.cyril.mobilespeedometer.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.cyril.mobilespeedometer.Model.Result.Result
import com.example.cyril.mobilespeedometer.R
import kotlinx.android.synthetic.main.row_layout.view.*

class ListResultsAdapter (internal var activity: Activity,
                          internal var results: List<Result>): BaseAdapter() {

    internal var inflater:LayoutInflater

    init {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rowView: View
        rowView = inflater.inflate(R.layout.row_layout, null)

        val dateRes = results[p0].date.toString()
        val timeRes = results[p0].time.toString()
        val Res = results[p0].resultTime.toString()
        rowView.item_textView.text = "$dateRes, $timeRes, $Res"

        return rowView
    }

    override fun getItem(p0: Int): Any {
        return results[p0]
    }

    override fun getItemId(p0: Int): Long {
        return results[p0].id.toLong()
    }

    override fun getCount(): Int {
        return results.size
    }
}