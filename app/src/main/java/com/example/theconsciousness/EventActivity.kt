package com.example.theconsciousness

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.theconsciousness.Models.Quote
import com.example.theconsciousness.databinding.ActivityEventBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.akadoshi.setOnClickListener {
            val intent = Intent(this, EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Ekadashi")
            startActivity(intent)
        }
        binding.tvmarriage.setOnClickListener {
            val intent = Intent(this, EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Marriage Date")
            startActivity(intent)
        }
        binding.tvpuja.setOnClickListener {
            val intent = Intent(this, EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Puja")
            startActivity(intent)
        }
        binding.tvGrohon.setOnClickListener {
            val intent = Intent(this, EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Grohon")
            startActivity(intent)
        }

        binding.tvAmPu.setOnClickListener {
            val intent = Intent(this, EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Purnima")
            startActivity(intent)
        }
        binding.tvothers.setOnClickListener {
            val intent = Intent(this, EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Others")
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            showConfirmationDialog()

        }
        if (isNetworkAvailable(this)) {
            // Internet is available, retrieve data
            getQuote()
        } else {
            // No internet connection, show dialog
            showNoInternetDialog()
        }



    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showNoInternetDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setIcon(R.drawable.round_signal_wifi_connected_no_internet_4_24)
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()
    }
    private fun getQuote() {
        val reff = FirebaseDatabase.getInstance().reference.child("Quote")
        val query = reff.orderByChild("timestamp").limitToLast(1)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val datt = snap.getValue(Quote::class.java)
                        if (datt != null) {
                            binding.tvquote.text = datt.quote
                            binding.tvquoter.text = datt.author
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }
    fun showConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val etConfirmCode: EditText = dialogView.findViewById(R.id.etConfirmCode)
        val btnConfirm: Button = dialogView.findViewById(R.id.btnConfirm)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirmation Code")
            .setCancelable(true)
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        btnConfirm.setOnClickListener {
            val confirmCode = etConfirmCode.text.toString()
            if (confirmCode == "12355") {
                startActivity(Intent(this, AddEventActivity::class.java))
                dialog.dismiss()
            } else {
                // Show an error message or handle incorrect confirmation code
                etConfirmCode.error = "Incorrect confirmation code"
            }
        }

        dialog.show()
    }


}