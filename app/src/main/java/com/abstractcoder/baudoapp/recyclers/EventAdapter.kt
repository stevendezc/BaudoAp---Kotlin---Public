package com.abstractcoder.baudoapp.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.EventListItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(private val eventList: ArrayList<EventMain>) : RecyclerView.Adapter<EventAdapter.EventHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.event_list_item,
            parent, false
        )
        return EventHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val currentItem = eventList[position]

        val dateFormat = SimpleDateFormat("dd 'de' MMMM '-' yyyy", Locale("es", "ES"))
        holder.eventDate.text = dateFormat.format(currentItem.date?.toDate() ?: null)
        holder.eventTitle.text = currentItem.title.toString()
        holder.eventDescription.text = currentItem.description.toString()

        holder.reminderButton.setOnClickListener {
            println("SET REMINDER")
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class EventHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = EventListItemBinding.bind(itemView)

        val eventDate = binding.eventDate
        val eventTitle = binding.eventTitle
        val eventDescription = binding.eventDescription

        val eventButton = binding.eventButton
        val reminderButton = binding.reminderButton
    }
}