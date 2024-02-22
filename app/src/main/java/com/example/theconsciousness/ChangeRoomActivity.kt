package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.ChangeRoomAdapter
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.databinding.ActivityChangeRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChangeRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeRoomBinding
    private lateinit var adapter: ChangeRoomAdapter
    private lateinit var guruList: ArrayList<User>
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val llayoutManager = LinearLayoutManager(this)
        llayoutManager.reverseLayout = true
        llayoutManager.stackFromEnd = true
        binding.recyclerChange.layoutManager = llayoutManager
        guruList = ArrayList()

        val databaseReference =
            auth.currentUser?.let { FirebaseDatabase.getInstance().getReference("MyGuru").child(it.uid) }

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val guruIds = ArrayList<String>()
                    for (guruSnapshot in snapshot.children) {
                        val guruId = guruSnapshot.key
                        if (guruId != null) {
                            guruIds.add(guruId)
                        }
                    }

                    // Now you have the list of guru IDs (guruIds)
                    // Proceed to fetch guru data for each ID
                    fetchGuruData(guruIds)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        adapter  = ChangeRoomAdapter(this,guruList)
        binding.recyclerChange.adapter = adapter
        if (guruList.isEmpty()){
            binding.noListConstant.visibility = View.VISIBLE
        }
        binding.searchScolar.setOnClickListener {
            startActivity(Intent(this,SearchScholarActivity::class.java))
        }

    }
    private fun fetchGuruData(guruIds: List<String>) {
        for (guruId in guruIds) {
            val guruReference = FirebaseDatabase.getInstance().getReference("Users").child(guruId)
            guruReference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val guru = snapshot.getValue(User::class.java)
                        if (guru != null) {
                            guruList.add(guru)
                            if (guruList.size == guruIds.size) {
                                adapter.notifyDataSetChanged()
                                binding.noListConstant.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}