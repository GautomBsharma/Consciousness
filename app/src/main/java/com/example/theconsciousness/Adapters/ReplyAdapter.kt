package com.example.theconsciousness.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.theconsciousness.Models.Replay
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ReplyAdapter(var context: Context, var replyList: ArrayList<Replay>):RecyclerView.Adapter<ReplyAdapter.ReplyHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reply_item,parent,false)
        return ReplyHolder(view)
    }

    override fun getItemCount(): Int {
       return replyList.size
    }

    override fun onBindViewHolder(holder: ReplyHolder, position: Int) {
        val data = replyList[position]
        holder.reply.text = data.reply
        publisher(data.publisher,holder.pubImage,holder.userName)
    }

    private fun publisher(publisher: String, pubImage: CircleImageView?, userName: TextView?) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.UserImageUrl).placeholder(R.drawable.profile).into(pubImage)
                    userName!!.text =(user.UserName)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    inner class ReplyHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val pubImage = itemView.findViewById<CircleImageView>(R.id.profileImage)
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val reply = itemView.findViewById<TextView>(R.id.replay)
    }

}