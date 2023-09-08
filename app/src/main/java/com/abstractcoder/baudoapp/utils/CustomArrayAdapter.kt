package com.pereira.baudoapp.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

class CustomArrayAdapter(
    context: Context,
    resource: Int,
    objects: Array<String>
) : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        // Check if the item is selected
        val selectedItemPosition = (parent as Spinner).selectedItemPosition
        if (position == selectedItemPosition) {
            textView.setTextColor(Color.WHITE)
            textView.textSize = 12f
        }

        return view
    }
}