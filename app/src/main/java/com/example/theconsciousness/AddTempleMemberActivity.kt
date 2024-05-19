package com.example.theconsciousness

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.theconsciousness.databinding.ActivityAddTempleMemberBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddTempleMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTempleMemberBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var imageUri : Uri?=null
    private var uid =""
    private var templeId =""

    private var launchGelaryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            imageUri = it.data!!.data
            binding.postimage.setImageURI(imageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTempleMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        templeId = intent.getStringExtra("TEMPLE_ID").toString()
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
            if ( imageUri.toString().isEmpty()) {
                Toast.makeText(this, "Please Upload member Image", Toast.LENGTH_SHORT).show()
            }

            else if (binding.memberName.text.toString().isEmpty()){
                binding.memberName.error = "Enter Member Name"
            }
            else if (binding.templeMemberAddress.text.toString().isEmpty()){
                binding.templeMemberAddress.error = "Enter Member about info"
            }
            else if (binding.membercontent.text.toString().isEmpty()){
                binding.membercontent.error = "Enter contract info"
            }

            else{
                    uploadImages(imageUri!!)
                    dialog.show()
                }
            
        }
    }

    private fun uploadImages(uri: Uri) {

        val fileName = UUID.randomUUID().toString()+".jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("TempleMemberImage/$fileName")
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
        val dbRef = FirebaseDatabase.getInstance().reference.child("TempleMember").child(templeId)
        val postId= dbRef.push().key
        val updateMap = HashMap<String,Any>()
        updateMap["templeMemImageUrl"] = imageUrl
        updateMap["templeMemberId"] = postId.toString()
        updateMap["memberInTemple"] = templeId
        updateMap["templeMemberName"] = binding.memberName.text.toString()
        updateMap["templeMemberBio"] = binding.templeMemberAddress.text.toString()
        updateMap["templeMemberContent"] = binding.membercontent.text.toString()


        if (postId != null) {
            dbRef.child(postId).setValue(updateMap).addOnSuccessListener {
                Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "member Added  Fail", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }
}