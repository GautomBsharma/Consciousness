package com.example.theconsciousness.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.theconsciousness.AddEventActivity
import com.example.theconsciousness.EventShowActivity
import com.example.theconsciousness.Models.Quote
import com.example.theconsciousness.R
import com.example.theconsciousness.databinding.FragmentEventsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class EventsFragment : Fragment() {
    private lateinit var binding: FragmentEventsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventsBinding.inflate(layoutInflater)
        binding.akadoshi.setOnClickListener {
            val intent = Intent(requireContext(), EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Ekadashi")
            startActivity(intent)
        }
        binding.tvmarriage.setOnClickListener {
            val intent = Intent(requireContext(), EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Marriage Date")
            startActivity(intent)
        }
        binding.tvpuja.setOnClickListener {
            val intent = Intent(requireContext(), EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Puja")
            startActivity(intent)
        }
        binding.tvGrohon.setOnClickListener {
            val intent = Intent(requireContext(), EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Grohon")
            startActivity(intent)
        }

        binding.tvAmPu.setOnClickListener {
            val intent = Intent(requireContext(), EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Purnima")
            startActivity(intent)
        }
        binding.tvothers.setOnClickListener {
            val intent = Intent(requireContext(), EventShowActivity::class.java)
            intent.putExtra("EVENT_TITLE", "Others")
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            showConfirmationDialog()

        }


        getQuote()
        return binding.root
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

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Confirmation Code")
            .setCancelable(true)
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        btnConfirm.setOnClickListener {
            val confirmCode = etConfirmCode.text.toString()
            if (confirmCode == "12355") {
                startActivity(Intent(requireContext(), AddEventActivity::class.java))
                dialog.dismiss()
            } else {
                // Show an error message or handle incorrect confirmation code
                etConfirmCode.error = "Incorrect confirmation code"
            }
        }

        dialog.show()
    }


}