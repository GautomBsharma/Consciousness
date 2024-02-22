package com.example.theconsciousness

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.theconsciousness.databinding.ActivityEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var dialog: Dialog
    private lateinit var auth: FirebaseAuth
    private var imageUri : Uri?=null
    private var selectedItem :Any = ""
    private var launchGelaryActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            imageUri = it.data!!.data
            binding.imageEdit.setImageURI(imageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val status = resources.getStringArray(R.array.status)
        val arrayAdapter = ArrayAdapter(this,R.layout.drop_doun_item,status)
        binding.autoText.setAdapter(arrayAdapter)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        auth = Firebase.auth
        binding.autoText.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

            selectedItem = adapterView.getItemAtPosition(i)

        }
        binding.selectBtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGelaryActivity.launch(intent)
        }
        binding.UploadBtn.setOnClickListener {

            validateData()
        }

    }
    private fun validateData() {
        if (binding.editName.text.toString().isEmpty()){
            binding.editName.error = "Enter Name"
        }
        else if (binding.editBio.text.toString().isEmpty()){
            binding.editBio.error = "Enter Bio"
        }

        else if (binding.editBio.text!!.length>200){
            binding.editBio.error = "200< not allowed"
        }
        else if (selectedItem.toString().isEmpty()){
            Toast.makeText(this, "Select Status", Toast.LENGTH_SHORT).show()
        }
        else if (imageUri==null)
        {
            Toast.makeText(this, "Pick Profile Image", Toast.LENGTH_SHORT).show()
        }
        else{

            uploadImages(imageUri!!)
            dialog.show()
        }

    }
    private fun uploadImages(uri: Uri) {
        val fileName = UUID.randomUUID().toString()+".jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("ProfileImage/$fileName")
        storageRef.putFile(uri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {image->
                storeData(image.toString())
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Upload Storage Fail", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
    private fun storeData(imageUrl: String) {
        val userId = auth.currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().reference.child("Users")
        val updateMap = HashMap<String,Any>()
        updateMap["UserImageUrl"] = imageUrl
        updateMap["UserId"] = userId
        updateMap["UserName"] = binding.editName.text.toString()
        updateMap["UserStatus"] = selectedItem.toString()
        updateMap["UserBio"] = binding.editBio.text.toString()

        dbRef.child(userId).updateChildren(updateMap).addOnSuccessListener {
            Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }.addOnFailureListener {
            Toast.makeText(this, "Update Fail", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }
}