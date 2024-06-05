package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.MyPostAdapter
import com.example.theconsciousness.Models.PrayerPost
import com.example.theconsciousness.databinding.ActivityMyPrayerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyPrayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyPrayerBinding
    private var adapter: MyPostAdapter?= null
    private lateinit var auth: FirebaseAuth
    private var uid : String= ""
    private var datalist :MutableList<PrayerPost>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        val linearlayoutManager= LinearLayoutManager(this)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true
        auth = FirebaseAuth.getInstance()
        binding.recycleMyPrayer.layoutManager=linearlayoutManager
        datalist=ArrayList()
        adapter= MyPostAdapter(this, datalist as ArrayList<PrayerPost>)
        binding.recycleMyPrayer.adapter=adapter

        if (isNetworkAvailable(this)) {
            // Internet is available, retrieve data
            retriveBlog()
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
    private fun retriveBlog() {
        val blgRef = FirebaseDatabase.getInstance().reference.child("posts")
        blgRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    datalist!!.clear()
                    for (snap in snapshot.children){

                        val rent = snap.getValue(PrayerPost::class.java)
                        if (rent != null) {
                            if(rent.UserId == uid){
                                datalist!!.add(rent)
                            }

                        }

                    }
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}