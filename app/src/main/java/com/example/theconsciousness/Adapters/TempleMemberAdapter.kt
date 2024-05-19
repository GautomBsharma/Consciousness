package com.example.theconsciousness.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.theconsciousness.Models.TempleMember

import com.example.theconsciousness.R
import com.google.android.material.imageview.ShapeableImageView

class TempleMemberAdapter(var context: Context,var tempMemList: ArrayList<TempleMember>) : RecyclerView.Adapter<TempleMemberAdapter.TempleMemberHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempleMemberAdapter.TempleMemberHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.temple_member_item,parent,false)
        return TempleMemberHolder(view)
    }

    override fun getItemCount(): Int {
        return tempMemList.size
    }

    override fun onBindViewHolder(holder: TempleMemberAdapter.TempleMemberHolder, position: Int) {
        val data = tempMemList[position]

        holder.templeMemberName.text = data.templeMemberName
        holder.templeAboutMem.text = data.templeMemberBio
        holder.TemMemCont.text = data.templeMemberContent
        if (data.templeMemImageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(data.templeMemImageUrl)
                .placeholder(R.drawable.guru)
                .into(holder.templeMemberImg)
        } else {
            holder.templeMemberImg.setImageResource(R.drawable.guru)
        }
    }
    inner class TempleMemberHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val templeMemberImg = itemView.findViewById<ShapeableImageView>(R.id.templeMemberImg)
        val templeMemberName = itemView.findViewById<TextView>(R.id.tvTempleMemName)
        val templeAboutMem = itemView.findViewById<TextView>(R.id.tvAboutMember)
        val TemMemCont = itemView.findViewById<TextView>(R.id.tvMemberContact)
    }
}