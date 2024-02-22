package com.example.theconsciousness

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.GuruAdapter
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.databinding.ActivitySearchScholarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchScholarActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchScholarBinding
    private lateinit var adapter: GuruAdapter
    private lateinit var guruList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScholarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val llayoutManager = LinearLayoutManager(this)
        llayoutManager.reverseLayout = true
        llayoutManager.stackFromEnd = true
        binding.recycleScol.layoutManager = llayoutManager
        guruList = ArrayList()
        adapter  = GuruAdapter(this,guruList)
        binding.recycleScol.adapter = adapter
        binding.searchGro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchGro.text.toString() == "")
                {

                }
                else {
                    searchUser(s.toString().lowercase())
                }
            }
        })
        getScolar()
    }
    private fun getScolar() {
        val reff = FirebaseDatabase.getInstance().reference.child("Users")
        reff.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedList = ArrayList<User>() // Create a new list to store updated data
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val datt = snap.getValue(User::class.java)
                        if (datt?.UserStatus == "Scholar") {
                            updatedList.add(datt) // Add scholar users to the updated list
                        }
                    }
                    // Update guruList with the new data
                    guruList.clear()
                    guruList.addAll(updatedList)
                    adapter.notifyDataSetChanged() // Notify the adapter of changes
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }
    private fun searchUser(input:String) {
        val query= FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("UserName")
            .startAt(input)
            .endAt(input + "\uf8ff")
        query.addValueEventListener(object: ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                guruList.clear()
                for(snapshot in datasnapshot.children)
                {
                    val user=snapshot.getValue(User::class.java)
                    if (user != null) {
                        if(user.UserStatus == "Scholar") {
                            guruList.add(user)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}