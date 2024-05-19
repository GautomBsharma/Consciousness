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
import com.example.theconsciousness.databinding.ActivityAddTempleBinding
import com.example.theconsciousness.databinding.ActivityTempleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddTempleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTempleBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var selectedItem :Any = ""
    private var imageUri : Uri?=null
    private var uid =""
    private var launchGelaryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            imageUri = it.data!!.data
            binding.postimage.setImageURI(imageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTempleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        uid= auth.currentUser?.uid.toString()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        binding.pickImage.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGelaryActivity.launch(intent)

        }
        binding.btnUp.setOnClickListener {
            if (selectedItem.toString().isEmpty()) {
                Toast.makeText(this, "Select Course must", Toast.LENGTH_SHORT).show()
            } else {
                if (imageUri == null || imageUri.toString().isEmpty()) {
                    storeData2()
                } else {
                    uploadImages(imageUri!!)
                    dialog.show()
                }
            }
        }
    }
    private fun storeData2() {
        val dbRef = FirebaseDatabase.getInstance().reference.child("Temple")
        val postId= dbRef.push().key
        val updateMap = HashMap<String,Any>()
        updateMap["templeImageUrl"] = ""
        updateMap["templeId"] = postId.toString()
        updateMap["adminTemple"] = uid
        updateMap["templeName"] = binding.templeName.text.toString()
        updateMap["templeAddress"] = binding.templeAddress.text.toString()

        if (postId != null) {
            dbRef.child(postId).setValue(updateMap).addOnSuccessListener {
                Toast.makeText(this, "temple added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "temple  Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }

    private fun uploadImages(uri: Uri) {

        val fileName = UUID.randomUUID().toString()+".jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("TempleImage/$fileName")
        storageRef.putFile(uri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {image->
                storeData(image.toString())
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Upload Storage Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
    }
    private fun storeData(imageUrl: String) {
        val dbRef = FirebaseDatabase.getInstance().reference.child("Temple")
        val postId= dbRef.push().key
        val updateMap = HashMap<String,Any>()
        updateMap["templeImageUrl"] = imageUrl
        updateMap["templeId"] = postId.toString()
        updateMap["adminTemple"] = uid
        updateMap["templeName"] = binding.templeName.text.toString()
        updateMap["templeAddress"] = binding.templeAddress.text.toString()

        if (postId != null) {
            dbRef.child(postId).setValue(updateMap).addOnSuccessListener {
                Toast.makeText(this, "Temple added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Temple  Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
}