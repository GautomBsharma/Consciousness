package com.example.theconsciousness

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var selectedItem :Any = ""
    private var fcmToken :String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        val district = resources.getStringArray(R.array.status)
        val arrayAdapter = ArrayAdapter(this,R.layout.drop_doun_item,district)
        binding.autoText.setAdapter(arrayAdapter)
        binding.autoText.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            selectedItem = adapterView.getItemAtPosition(i)

        }
        binding.sinBtn.setOnClickListener {
            validateData()
        }
        binding.goLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
    private fun validateData() {
        if (binding.sinName.text.toString().isEmpty()){
            binding.sinName.error = "Enter Name"
        }
        else if (binding.sinEmail.text.toString().isEmpty()){
            binding.sinEmail.error = "Enter Email A/C"
        }
        else if (binding.sinPassword.text!!.length<6){
            binding.sinPassword.error = "Enter 6 or more!"
        }
        else if (binding.sinbio.text.toString().isEmpty()){
            binding.sinbio.error = "Enter bio"
        }
        else if (selectedItem.toString().isEmpty()){
            Toast.makeText(this, "Select Blood group", Toast.LENGTH_SHORT).show()
        }
        else if (binding.sinPassword.text.toString().isEmpty()){
            binding.sinPassword.error = "Enter password"
        }
        else{
            creatAcount()
            dialog.show()
        }
    }
    private fun creatAcount() {
        val email = binding.sinEmail.text.toString()
        val password = binding.sinPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    getToken()
                } else
                {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
    }
    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result
                saveUser(fcmToken)
            } else {
                Toast.makeText(
                    baseContext, "get token fail",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun saveUser(fcmToken: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        val UserId = auth.currentUser!!.uid
        userMap["UserImageUrl"] = ""
        userMap["UserId"] = UserId
        userMap["UserName"] = binding.sinName.text.toString().lowercase(Locale.ROOT)
        userMap["UserPhone"] = ""
        userMap["UserToken"] = fcmToken
        userMap["UserBio"] = binding.sinbio.text.toString().lowercase()
        userMap["UserInstitute"] = ""
        userMap["UserEmail"] = binding.sinEmail.text.toString()
        userMap["UserStatus"] = selectedItem.toString()
        userMap["UserDistrict"] = ""
        userRef.child(UserId).setValue(userMap).addOnCompleteListener {
            startActivity(Intent(this, MainActivity::class.java))
            dialog.dismiss()
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                baseContext, "Save data fail",
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
        }
    }
}