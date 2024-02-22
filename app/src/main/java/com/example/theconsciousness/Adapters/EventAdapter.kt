package com.example.theconsciousness.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.theconsciousness.Models.Event
import com.example.theconsciousness.R

class EventAdapter(var context: Context,var eventList:ArrayList<Event>): RecyclerView.Adapter<EventAdapter.EventHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_item,parent,false)
        return EventHolder(view)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val data = eventList[position]
        holder.evname.text = data.eName
       holder.edescription.text = data.eDescription
    }
    inner class EventHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val evname = itemView.findViewById<TextView>(R.id.tvEvent)
        val edescription = itemView.findViewById<TextView>(R.id.evDescription)


    }
}