package com.example.theconsciousness

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.theconsciousness.databinding.ActivityAddBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

class AddBlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBlogBinding
    private lateinit var auth :FirebaseAuth
    private lateinit var dialog: Dialog
    private var imageUri : Uri?=null

    private var launchGelaryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            imageUri = it.data!!.data
            binding.postimage.setImageURI(imageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)
        auth = FirebaseAuth.getInstance()

        binding.btnUp.setOnClickListener {
                 validateData2()
        }
        binding.piokImage.setOnClickListener {

            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGelaryActivity.launch(intent)
            binding.postimage.visibility = View.VISIBLE
        }
    }

    private fun validateData2() {
         if (imageUri.toString().isEmpty()&& binding.postdescr.text.toString().isEmpty()){
            binding.postdescr.error = "Enter Title"
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show()
        }

        else{
            uploadImages2(imageUri!!)
            dialog.show()
        }

    }


    private fun uploadImages2(uri: Uri) {
        val fileName = UUID.randomUUID().toString()+".jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("PostPrayerImage/$fileName")
        storageRef.putFile(uri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {image->
                storeData2(image.toString())
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Upload Storage Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }


    private fun storeData2(imageUrl: String) {
        val userId = auth.currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().reference.child("posts")
        val postId= dbRef.push().key
        val uplaodTime = System.currentTimeMillis()
        val postMap = java.util.HashMap<String, Any>()
        postMap["prayerImageUrl"] = imageUrl
        postMap["UserId"] = userId
        postMap["uplaodTime"] = uplaodTime
        postMap["postId"] = postId.toString()
        postMap["post"] = binding.postdescr.text.toString()
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