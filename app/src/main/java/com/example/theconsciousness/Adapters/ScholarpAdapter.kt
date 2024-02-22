package com.example.theconsciousness.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.theconsciousness.Models.ScholarPost
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.R
import com.example.theconsciousness.ReplayScholarActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ScholarpAdapter(val context: Context,val scholarpostList:ArrayList<ScholarPost>):RecyclerView.Adapter<ScholarpAdapter.ScholarpHolder>(){

    private var firebaseUser: FirebaseUser?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScholarpHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.my_home_item,parent,false)
        return ScholarpHolder(view)
    }

    override fun getItemCount(): Int {
       return scholarpostList.size
    }

    override fun onBindViewHolder(holder: ScholarpHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val data = scholarpostList[position]
        val scholarpId = data.scholarpostId
        holder.homePost.text = data.scholarpost
        frofileinfo(holder.homePro,data.UserId)


        holder.replay.setOnClickListener {
            val intent = Intent(context, ReplayScholarActivity::class.java).apply {
                putExtra("BLOG_ID",scholarpId)
            }
            context.startActivity(intent)
        }

    }
    private fun frofileinfo(
        profileIm: CircleImageView?,
        userId: String?
    ) {
        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user.UserImageUrl.isNotEmpty()) {
                            Picasso.get().load(user.UserImageUrl).placeholder(R.drawable.guru).into(profileIm)
                        } else {
                            profileIm?.setImageResource(R.drawable.guru)
                        }
                    }
                }
            }
        })
    }
    inner class ScholarpHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val homePro = itemView.findViewById<CircleImageView>(R.id.homeProfile)
        val homePost = itemView.findViewById<TextView>(R.id.tvmymessage)
        val hreplayCount = itemView.findViewById<TextView>(R.id.homereplayCount)
        val replay = itemView.findViewById<ImageView>(R.id.homeReplay)

    }

}