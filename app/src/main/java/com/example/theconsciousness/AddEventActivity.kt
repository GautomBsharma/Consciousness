package com.example.theconsciousness

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivityAddEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.ktx.Firebase

class AddEventActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddEventBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var selectedItem :Any = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        auth = Firebase.auth
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        val district = resources.getStringArray(R.array.event_title)
        val arrayAdapter = ArrayAdapter(this,R.layout.drop_doun_item,district)
        binding.autoText.setAdapter(arrayAdapter)
        binding.autoText.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            selectedItem = adapterView.getItemAtPosition(i)

        }
        binding.upquopte.setOnClickListener {
            validateDat()
        }
        binding.uploadEvent.setOnClickListener {
            validateevent()
        }
        setContentView(binding.root)
    }

    private fun validateevent() {
        if(binding.eventName.text.toString().isEmpty()){
            binding.eventName.error = "Enter Event Name"
        }
        else if (selectedItem.toString().isEmpty())
        {
            Toast.makeText(this, "Select Event Title ", Toast.LENGTH_SHORT).show()
        }
        else if (binding.evDescription.text.toString().isEmpty()){
            binding.evDescription.error = "Enter Description"
        }
        else{
            saveeventdata()
        }
    }

    private fun saveeventdata() {
        val db = FirebaseDatabase.getInstance().reference.child("Events").child(selectedItem.toString())
        val postId= db.push().key.toString()
        val myMap = HashMap<String,Any>()

        myMap["eName"]= binding.eventName.text.toString()
        myMap["eventId"] = postId
        myMap["eDescription"] = binding.evDescription.text.toString()
        db.child(postId).setValue(myMap).addOnSuccessListener {
            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()

        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun validateDat() {
        if(binding.inquote.text.toString().isEmpty()){
            binding.inquote.error = "Enter Quote"
        }
        else if (binding.inquoteowner.text.toString().isEmpty())
        {
            binding.inquoteowner.error = "Enter Author"
        }
        else{
            savedata()
        }
    }

    private fun savedata() {
        val db = FirebaseDatabase.getInstance().reference.child("Quote")
        val myMap = HashMap<String, Any>()
        val keyId = db.push().key
        val timestamp = System.currentTimeMillis().toString()
        myMap["quote"] = binding.inquote.text.toString()
        myMap["QuoteId"] = keyId.toString()
        myMap["author"] = binding.inquoteowner.text.toString()
        myMap["timestamp"] = timestamp
        db.child(timestamp).setValue(myMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }
}