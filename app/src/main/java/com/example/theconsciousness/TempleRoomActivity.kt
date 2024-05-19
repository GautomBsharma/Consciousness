package com.example.theconsciousness



import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.TempleMemberAdapter
import com.example.theconsciousness.Models.TempleMember
import com.example.theconsciousness.Models.TempleNotice
import com.example.theconsciousness.databinding.ActivityTempleRoomBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TempleRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTempleRoomBinding
    private var adapter: TempleMemberAdapter?=null
    private var tempMemList:ArrayList<TempleMember>?=null

    private var templeId:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempleRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        templeId = intent.getStringExtra("TEMPLE_ID").toString()
        val templeName = intent.getStringExtra("TEMPLE_NAME")


        binding.templeName.text = templeName


        val linearlayoutManager= LinearLayoutManager(this)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true
        binding.memberRecycle.layoutManager=linearlayoutManager
        tempMemList=ArrayList()
        adapter = TempleMemberAdapter(this, tempMemList!!)
        binding.memberRecycle.setHasFixedSize(true)
        binding.memberRecycle.setItemViewCacheSize(15)
        binding.memberRecycle.adapter=adapter
        binding.editNotice.setOnClickListener {
            val intent = Intent(this,AddNoticeActivity::class.java)
            intent.putExtra("TEMPLE_ID",templeId)
        }
        binding.editTempleMember.setOnClickListener {
            val intent = Intent(this,AddTempleMemberActivity::class.java)
            intent.putExtra("TEMPLE_ID",templeId)
        }


        getNotice()
        getMember()

    }

    private fun getMember() {
        val blgRef = FirebaseDatabase.getInstance().reference.child("TempleMember").child(templeId)
        blgRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    tempMemList!!.clear()
                    for (snap in snapshot.children){
                        val rent = snap.getValue(TempleMember::class.java)
                        tempMemList!!.add(rent!!)
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getNotice() {
        val reff = FirebaseDatabase.getInstance().reference.child("TempleNotice").child(templeId)
        val query = reff.orderByChild("timestamp").limitToLast(1)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val datt = snap.getValue(TempleNotice::class.java)
                        if (datt != null) {
                            binding.tvNotice.text = datt.templeNotice
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }
}