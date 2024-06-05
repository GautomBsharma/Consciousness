package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

        if (isNetworkAvailable(this)) {
            // Internet is available, retrieve data
            getEvent()
        } else {
            // No internet connection, show dialog
            showNoInternetDialog()
        }

    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showNoInternetDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setIcon(R.drawable.round_signal_wifi_connected_no_internet_4_24)
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()
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