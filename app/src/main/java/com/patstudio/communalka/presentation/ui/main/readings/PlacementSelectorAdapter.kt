package com.patstudio.communalka.presentation.ui.main.readings


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement


class PlacementSelectorAdapter(var context: Context, var placements: ArrayList<Placement>) :
    BaseAdapter() {

    var inflter: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return placements.size
    }

    override fun getItem(i: Int): Placement {
        return placements.get(i)
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        view = inflter.inflate(R.layout.item_placement_selector, null)
//        val icon: ImageView = view.findViewById<View>(R.id.imageView) as ImageView
//        val names = view.findViewById<View>(R.id.textView) as TextView
//        icon.setImageResource(flags[i])
//        names.text = countryNames[i]
        return view
    }

}