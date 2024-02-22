package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.EventAdapter
import com.example.theconsciousness.Models.Event
import com.example.theconsciousness.Models.Quote
import com.example.theconsciousness.databinding.ActivityEventBinding
import com.example.theconsciousness.databinding.ActivityEventShowBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventShowActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEventShowBinding
    private lateinit var adapter: EventAdapter
    private lateinit var eventList:ArrayList<Event>
    private var eventTitle :String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclevent.layoutManager = LinearLayoutManager(this)
        eventList =  ArrayList()
        adapter = EventAdapter(this,eventList)
        binding.recyclevent.adapter = adapter
        eventTitle = intent.getStringExtra("EVENT_TITLE").toString()
        getEvent()
    }

    private fun getEvent() {
        val reff = FirebaseDatabase.getInstance().reference.child("Events").child(eventTitle)
        reff.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    eventList.clear()
                    for (snap in snapshot.children){
                        val datt = snap.getValue(Event::class.java)
                        if (datt != null) {
                            eventList.add(datt)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}