package com.example.theconsciousness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.theconsciousness.DataHelperClasess.DatabaseHelper
import com.example.theconsciousness.Models.Note
import com.example.theconsciousness.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var db: DatabaseHelper
    private var notid: Int = -1
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        db = DatabaseHelper(this)
        notid = intent.getIntExtra("ID", -1)
        if (notid == -1) {
            finish()
            return
        }
        note = db.getNotebyId(notid)

        binding.unotes.setText(note.notedes)
        binding.AddUpdateNote.setOnClickListener {
            val newdes = binding.unotes.text.toString()
            val updatedNote = note.copy(notedes = newdes)
            db.updateNote(updatedNote)
            finish()
            Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show()
        }
    }
}