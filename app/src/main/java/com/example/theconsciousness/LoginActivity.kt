package com.example.theconsciousness

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.theconsciousness.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(true)
        binding.goSing.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.logBtn.setOnClickListener {
            validatedata()
        }
    }

    private fun validatedata() {
        if (binding.logEmail.text.toString().isEmpty()){
            binding.logEmail.error = "Enter Email A/C"
        }
        else if (binding.logPassword.text!!.length<6){
            binding.logPassword.error = "Enter 6 or more!"
        }
        else if (binding.logPassword.text.toString().isEmpty()){
            binding.logPassword.error = "Enter password"
        }
        else{
            login()
            dialog.show()
        }
    }

    private fun login() {
        val email = binding.logEmail.text.toString()
        val password = binding.logPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                } else {

                    dialog.dismiss()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}