package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.ScholarpAdapter
import com.example.theconsciousness.Models.ScholarPost
import com.example.theconsciousness.databinding.ActivityMyHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ScholarpAdapter
    private lateinit var scholarpostList: ArrayList<ScholarPost>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val linearlayoutManager = LinearLayoutManager(this)
        linearlayoutManager.reverseLayout = true
        linearlayoutManager.stackFromEnd = true

        binding.myHomeRecyc.layoutManager = linearlayoutManager
        scholarpostList = ArrayList()
        adapter = ScholarpAdapter(this, scholarpostList)
        binding.myHomeRecyc.adapter = adapter
        binding.sendGmessage.setOnClickListener {
            startActivity(Intent(this,AddMessageActivity::class.java))
        }

        getHomePost()
    }

    private fun getHomePost() {

        val reff = auth.currentUser?.let {
            FirebaseDatabase.getInstance().reference.child("ScholarPostRoom").child(
                it.uid
            )
        }
        reff?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val dat = snap.getValue(ScholarPost::class.java)
                        if (dat != null) {
                            scholarpostList.add(dat)
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