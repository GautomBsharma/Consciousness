package com.example.theconsciousness.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.theconsciousness.Models.Temple
import com.example.theconsciousness.R
import com.example.theconsciousness.TempleRoomActivity
import com.google.android.material.imageview.ShapeableImageView

class TempleAdapter(var context: Context,var templeList: ArrayList<Temple>) : RecyclerView.Adapter<TempleAdapter.TempleHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempleHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.temple_item,parent,false)
        return TempleHolder(view)
    }

    override fun getItemCount(): Int {
        return templeList.size
    }

    override fun onBindViewHolder(holder: TempleHolder, position: Int) {
        val datap = templeList[position]
        holder.templeName.text = datap.templeName
        holder.templeAddress.text = datap.templeAddress
        if (datap.templeImageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(datap.templeImageUrl)
                .placeholder(R.drawable.templecon)
                .centerCrop()
                .into(holder.templeImg)
        } else {
            holder.templeImg.setImageResource(R.drawable.templecon)
        }
        holder.templeName.setOnClickListener {
            val intent = Intent(context,TempleRoomActivity::class.java)
            intent.putExtra("TEMPLE_ID",datap.templeId)
            intent.putExtra("TEMPLE_NAME",datap.templeName)
            context.startActivity(intent)
        }
    }
    inner class TempleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val templeImg = itemView.findViewById<ShapeableImageView>(R.id.templeImage)
        val templeName = itemView.findViewById<TextView>(R.id.templeName)
        val templeAddress = itemView.findViewById<TextView>(R.id.templeAddress)
        val goTemple = itemView.findViewById<TextView>(R.id.goTempleRoom)


    }
}