package com.example.theconsciousness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.ReplayScholarAdapter
import com.example.theconsciousness.Adapters.ReplyAdapter
import com.example.theconsciousness.Models.Replay
import com.example.theconsciousness.databinding.ActivityReplayScholarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReplayScholarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReplayScholarBinding
    private lateinit var auth: FirebaseAuth
    private var blogId :String = ""
    private var replyAdapter: ReplayScholarAdapter?=null
    private var replyscholarList :MutableList<Replay>?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReplayScholarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val linearlayoutManager= LinearLayoutManager(this)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true
        binding.recycleReply.layoutManager=linearlayoutManager
        replyscholarList=ArrayList()
        replyAdapter= ReplayScholarAdapter(this,replyscholarList as ArrayList<Replay>)
        binding.recycleReply.adapter=replyAdapter

        auth = FirebaseAuth.getInstance()
        blogId = intent.getStringExtra("BLOG_ID").toString()
        binding.sendBtn.setOnClickListener {
            validateData()
        }
        readReply(blogId)
    }

    private fun readReply(blogId: String) {
        val ref= FirebaseDatabase.getInstance().reference.child("ReplyScholarPost").child(blogId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    replyscholarList!!.clear()
                    for (snapso in snapshot.children){
                        val data = snapso.getValue(Replay::class.java)
                        replyscholarList?.add(data!!)
                    }
                    replyAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun validateData() {
        if (binding.addReply.text.toString().isEmpty()){
            binding.addReply.error = "Enter Reply"
        }
        else{
            savereplay(blogId)
        }
    }

    private fun savereplay(blogId: String) {

        val replayRef = FirebaseDatabase.getInstance().reference.child("ReplyScholarPost").child(blogId)
        val replayMap = HashMap<String,Any>()
        replayMap["reply"] = binding.addReply.text.toString()
        replayMap["publisher"] = auth.currentUser!!.uid

        replayRef.push().setValue(replayMap)
            .addOnSuccessListener {
                binding.addReply.text!!.clear()

            }.addOnFailureListener {
                Toast.makeText(this, "Reply not Added", Toast.LENGTH_SHORT).show()
            }
    }
}