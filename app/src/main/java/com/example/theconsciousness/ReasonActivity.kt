package com.example.theconsciousness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.NoteAdapter
import com.example.theconsciousness.DataHelperClasess.DatabaseHelper
import com.example.theconsciousness.databinding.ActivityReasonBinding

class ReasonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReasonBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: NoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReasonBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = DatabaseHelper(this)
        adapter = NoteAdapter(this,db.getallNote())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        adapter.refreshData(db.getallNote())
    }
}