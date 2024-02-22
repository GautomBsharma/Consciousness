package com.example.theconsciousness


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivityAddPostScholarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AddPostScholarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPostScholarBinding
    private lateinit var auth: FirebaseAuth
    private var currentRoomId : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostScholarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        currentRoomId = intent.getStringExtra("CURRENT_ROOM_ID").toString()
        binding.btnUp.setOnClickListener {
            validateData()
        }
    }
    private fun validateData() {
        if (binding.edMessage.text.toString().isEmpty()){
            binding.edMessage.error = "Enter message"
        }
        else{
            savedata()
        }
    }
    private fun savedata() {
        if (currentRoomId.isNotEmpty()){
            val userId = auth.currentUser?.uid.toString()
        val refff= FirebaseDatabase.getInstance().reference.child("ScholarPostRoom").child(currentRoomId)
        val MessageMap = HashMap<String,Any>()
        val postId= refff.push().key.toString()
        MessageMap["scholarpost"] = binding.edMessage.text.toString()
        MessageMap["uplaodTime"] = System.currentTimeMillis()
        MessageMap["UserId"] = userId
        MessageMap["scholarpostId"] = postId

        refff.child(postId).setValue(MessageMap).addOnSuccessListener {

            Toast.makeText(this, "Send successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this, " Failed", Toast.LENGTH_SHORT).show()
            }

        }
    }

}