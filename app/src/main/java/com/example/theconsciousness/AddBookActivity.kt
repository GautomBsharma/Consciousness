package com.example.theconsciousness

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivityAddBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        auth = FirebaseAuth.getInstance()

        binding.btnUp.setOnClickListener {

            validateData2()



        }

    }

    private fun validateData2() {
        if (binding.postdescr.text.toString().isEmpty()){
            binding.postdescr.error = "Enter Title (it use for searching)"
        }
        else if (binding.edblog.text.toString().isEmpty()){
            binding.edblog.error = "Enter blog"
        }
        else{
            storeData2()
            dialog.show()

        }

    }



    private fun storeData2() {
        val userId = auth.currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().reference.child("Blogs")
        val postId= dbRef.push().key
        val uplaodTime = System.currentTimeMillis()
        val postMap = HashMap<String, Any>()

        postMap["UserId"] = userId
        postMap["uplaodTime"] = uplaodTime
        postMap["blogId"] = postId.toString()
        postMap["title"] = binding.postdescr.text.toString().lowercase(Locale.ROOT)
        postMap["blog"] = binding.edblog.text.toString().lowercase(Locale.ROOT)
        postMap["refet"] = binding.reff.text.toString()

        if (postId != null) {
            dbRef.child(postId).setValue(postMap).addOnSuccessListener {
                Toast.makeText(this, "post added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Upload  Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
}