package com.example.theconsciousness



import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.TempleMemberAdapter
import com.example.theconsciousness.Models.TempleMember
import com.example.theconsciousness.Models.TempleNotice
import com.example.theconsciousness.databinding.ActivityTempleRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TempleRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTempleRoomBinding
    private var adapter: TempleMemberAdapter?=null
    private var tempMemList:ArrayList<TempleMember>?=null
    private lateinit var auth: FirebaseAuth
    private var templeId:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTempleRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        templeId = intent.getStringExtra("TEMPLE_ID").toString()
        val templeName = intent.getStringExtra("TEMPLE_NAME")
        auth = FirebaseAuth.getInstance()
        val uidd = auth.currentUser?.uid
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
            startActivity(intent)
        }
        binding.editTempleMember.setOnClickListener {
            val intent = Intent(this,AddTempleMemberActivity::class.java)
            intent.putExtra("TEMPLE_ID",templeId)
            startActivity(intent)
        }
        binding.deleteNotice.setOnClickListener {
            deleteNotice()
        }
        getAdmin(uidd)
        getNotice()
        getMember()
    }

    private fun deleteNotice() {
        val dbRef = FirebaseDatabase.getInstance().reference.child("TempleNotice").child(templeId)

        dbRef.orderByChild("timestamp").limitToLast(1).get().addOnSuccessListener { dataSnapshot ->
            for (noticeSnapshot in dataSnapshot.children) {
                noticeSnapshot.ref.removeValue().addOnSuccessListener {
                    binding.tvNotice.text = "Last Added Notice Deleted"
                    Toast.makeText(this, "Last Notice Deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to Delete Last Notice", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to Retrieve Last Notice", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAdmin(uidd: String?) {
        if (uidd == null) {
            // Handle the case where uidd is null
            return
        }

        val dbref = FirebaseDatabase.getInstance().reference.child("TempleAd min").child(templeId)
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var isAdmin = false
                    for (guruSnapshot in snapshot.children) {
                        val adminId = guruSnapshot.key
                        if (adminId == uidd) {
                            isAdmin = true
                            break
                        }
                    }

                    if (isAdmin) {
                        binding.editNotice.visibility = View.VISIBLE
                        binding.editTempleMember.visibility = View.VISIBLE
                        binding.deleteNotice.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately
                //Log.e("getAdmin", "Database error: ${error.message}")
            }
        })
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