package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.ScholarpAdapter
import com.example.theconsciousness.Fragments.*
import com.example.theconsciousness.Models.ScholarPost
import com.example.theconsciousness.databinding.ActivityScholarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScholarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScholarBinding
    private var currentRoomId:String = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ScholarpAdapter
    private lateinit var scholarpostList:ArrayList<ScholarPost>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityScholarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        getCurrentRoomId()
        val linearlayoutManager= LinearLayoutManager(this)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true

        binding.recycleGr.layoutManager=linearlayoutManager
        scholarpostList=ArrayList()
        adapter=  ScholarpAdapter(this,scholarpostList )
        binding.recycleGr.adapter=adapter

        getHomePost()
        binding.addhomePost.setOnClickListener {
            if (currentRoomId.isEmpty()){
                Toast.makeText(this, "Please set Current Room", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, AddPostScholarActivity::class.java)
                intent.putExtra("CURRENT_ROOM_ID", currentRoomId)
                startActivity(intent)
            }
        }
        binding.searchScolar.setOnClickListener {
            startActivity(Intent(this, SearchScholarActivity::class.java))
        }
        binding.myRoom.setOnClickListener {

            startActivity(Intent(this, MyHomeActivity::class.java))
        }

        binding.changegaccount.setOnClickListener {
            startActivity(Intent(this, ChangeRoomActivity::class.java))
        }

    }
    private fun getHomePost() {
        if (currentRoomId.isNotEmpty()){
            val reff = FirebaseDatabase.getInstance().reference.child("ScholarPostRoom").child(currentRoomId)
            reff.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        scholarpostList.clear()
                        for (snap in snapshot.children){
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


    private fun getCurrentRoomId() {
        val  uid = auth.currentUser?.uid
        val currentRoomRef = uid?.let {
            FirebaseDatabase.getInstance().getReference("CurrentRoom").child(
                it
            )
        }

        currentRoomRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children){
                        currentRoomId = snap.key.toString()
                    }
                } else {

                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}