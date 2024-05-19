package com.example.theconsciousness

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivityAddNoticeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class AddNoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoticeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var uid =""
    private var templeId =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoticeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        auth = Firebase.auth
        templeId = intent.getStringExtra("TEMPLE_ID").toString()
        uid = auth.currentUser?.uid.toString()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)

        binding.btnUp.setOnClickListener {
            if (binding.edNotice.text.toString().isEmpty()) {
                binding.edNotice.error = "Enter contract info"
            }
            else {
                storeNotic()
                dialog.show()
            }

        }
    }

    private fun storeNotic() {

        val dbRef = FirebaseDatabase.getInstance().reference.child("TempleNotice").child(templeId)
        val postId= dbRef.push().key
        val timestamp = System.currentTimeMillis().toString()
        val updateMap = HashMap<String,Any>()
        updateMap["templeNoticeId"] = postId.toString()
        updateMap["noticeInTemple"] = templeId
        updateMap["timestamp"] = timestamp
        updateMap["templeNotice"] = binding.edNotice.text.toString()



        if (postId != null) {
            dbRef.child(postId).setValue(updateMap).addOnSuccessListener {
                Toast.makeText(this, "Notice added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Notice Added  Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }

}