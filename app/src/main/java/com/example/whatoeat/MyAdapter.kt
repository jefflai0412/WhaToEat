package com.example.whatoeat

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, 0, items) {
    private var enabledPosition: Int = -1
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val textView: TextView = view.findViewById(R.id.text_item)
        textView.text = items[position]
        // Set background color if it's the enabled position
        if (position == enabledPosition) {
            view.setBackgroundColor(Color.parseColor("#FFB26B"))
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }
        return view
    }
    fun setEnabledPosition(position: Int) {
        enabledPosition = position
        notifyDataSetChanged() // Notify adapter that data set has changed
    }
}