package com.example.theconsciousness.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.theconsciousness.DataHelperClasess.DatabaseHelper
import com.example.theconsciousness.Models.Note
import com.example.theconsciousness.R
import com.example.theconsciousness.UpdateActivity
import de.hdodenhof.circleimageview.CircleImageView


class NoteAdapter (var context: Context, var notes:List<Note>): RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    private val db: DatabaseHelper = DatabaseHelper(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)
        return NoteHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val data = notes[position]
        holder.tvTitle.text = data.time
        holder.tvEvent.text = data.eventType
        holder.tvnote.text = data.notedes

        holder.editb.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java).apply {
                putExtra("ID", data.id)
            }
            context.startActivity(intent)
        }
        holder.delet.setOnClickListener {
            db.deleteNote(data.id)
            refreshData(db.getallNote())
            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
        }
    }

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvtitle)
        val tvnote = itemView.findViewById<TextView>(R.id.tvnote)
        val tvEvent = itemView.findViewById<TextView>(R.id.tvEvent)
        val editb = itemView.findViewById<CircleImageView>(R.id.editbtn)
        val delet = itemView.findViewById<CircleImageView>(R.id.btndela)

        init {
            // Get a random color from the list
            val randomColor = getRandomColor()

            // Set the background color of the item
            itemView.setBackgroundColor(randomColor)
        }

        // Function to get a random color from the provided list
        private fun getRandomColor(): Int {
            val colors = arrayOf(
                R.color.blogb,
                R.color.mainBack,
                R.color.raund1,
                R.color.raund2,
                R.color.purple,
                R.color.grad2
            )
            val randomIndex = (Math.random() * colors.size).toInt()
            return ContextCompat.getColor(itemView.context, colors[randomIndex])
        }
    }

    fun refreshData(newNote: List<Note>) {
        notes = newNote
        notifyDataSetChanged()
    }
}
