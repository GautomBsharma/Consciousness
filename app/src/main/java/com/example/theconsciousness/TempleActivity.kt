package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.BlogAdapter
import com.example.theconsciousness.Adapters.TempleAdapter
import com.example.theconsciousness.Models.Blog
import com.example.theconsciousness.Models.Temple
import com.example.theconsciousness.databinding.ActivityTempleBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TempleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTempleBinding

    private var adapter: TempleAdapter?=null
    private var templeList:ArrayList<Temple>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linearlayoutManager= LinearLayoutManager(this)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true
        binding.templeRecycle.layoutManager=linearlayoutManager
        templeList=ArrayList()
        adapter = TempleAdapter(this, templeList!!)
        binding.templeRecycle.setHasFixedSize(true)
        binding.templeRecycle.setItemViewCacheSize(15)
        binding.templeRecycle.adapter=adapter
        binding.addTemple.setOnClickListener {
            startActivity(Intent(this,AddTempleActivity::class.java))
        }
        binding.searchTemple.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchTemple.text.toString() == "")
                {
                    retriveTemple()
                }
                else {
                    searchTemple(s.toString().lowercase())
                }
            }
        })

        if (isNetworkAvailable(this)) {
            // Internet is available, retrieve data
            retriveTemple()
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
    private fun retriveTemple() {
        val blgRef = FirebaseDatabase.getInstance().reference.child("Temple")
        blgRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    templeList!!.clear()
                    for (snap in snapshot.children){
                        val rent = snap.getValue(Temple::class.java)
                        templeList!!.add(rent!!)
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun searchTemple(input:String) {
        val query= FirebaseDatabase.getInstance().reference
            .child("Temple")
            .orderByChild("templeName")
            .startAt(input)
            .endAt(input + "\uf8ff")
        query.addValueEventListener(object: ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                templeList?.clear()
                for(snapshot in datasnapshot.children)
                {
                    val temple=snapshot.getValue(Temple::class.java)
                    if(temple!=null)
                    {
                        templeList?.add(temple)
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }

}