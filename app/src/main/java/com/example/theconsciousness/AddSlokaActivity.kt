package com.example.theconsciousness

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivityAddSlokaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class AddSlokaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSlokaBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var selectedItem :Any = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSlokaBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = Firebase.auth
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        val district = resources.getStringArray(R.array.sloka_type)
        val arrayAdapter = ArrayAdapter(this,R.layout.drop_doun_item,district)
        binding.autoText.setAdapter(arrayAdapter)
        binding.autoText.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            selectedItem = adapterView.getItemAtPosition(i)

        }

        binding.btnUp.setOnClickListener {
            validateData()
        }
    }
    private fun validateData() {
        if(binding.slokaSource.text.toString().isEmpty()){
            binding.slokaSource.error = "Enter Sloka Source"
        }
        else if (selectedItem.toString().isEmpty())
        {
            Toast.makeText(this, "Select Sloka Type ", Toast.LENGTH_SHORT).show()
        }
        else if (binding.slokaSanskrit.text.toString().isEmpty()){
            binding.slokaSanskrit.error = "Enter Sanskrit Sloka"
        }
        else if (binding.slokaEnglish.text.toString().isEmpty()){
            binding.slokaEnglish.error = "Enter English Sloka"
        }


        else{
            saveSlokadata()
        }
    }

    private fun saveSlokadata() {
        val db = FirebaseDatabase.getInstance().reference.child("Sloka").child(selectedItem.toString())

        // Retrieve the number of children in selectedItem
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childCount = dataSnapshot.childrenCount.toInt()

                val myMap = HashMap<String, Any>()
                myMap["slokaSourse"] = binding.slokaSource.text.toString()
                myMap["slokaSanskrit"] = binding.slokaSanskrit.text.toString()
                myMap["slokaEnglish"] = binding.slokaEnglish.text.toString()
                myMap["slokaId"] = childCount // Set slokaId as the number of children

                // Push data to Firebase
                db.push().setValue(myMap).addOnSuccessListener {
                    Toast.makeText(this@AddSlokaActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@AddSlokaActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@AddSlokaActivity, "Failed to get child count", Toast.LENGTH_SHORT).show()
            }
        })
    }
}