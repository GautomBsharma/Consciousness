package com.example.theconsciousness.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.theconsciousness.Models.Blog
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.R
import com.example.theconsciousness.ReplayActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class SaveAdapter (var context: Context, var blogList :MutableList<Blog>): RecyclerView.Adapter<SaveAdapter.SaveHolder>() {

    private var firebaseUser: FirebaseUser?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.save_item,parent,false)
        return SaveHolder(view)
    }
    override fun getItemCount(): Int {
        return blogList.size
    }

    override fun onBindViewHolder(holder: SaveHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val data = blogList[position]
        val blogId = data.blogId
        holder.blog.text = data.blog
        holder.title.text = data.title
        holder.ref.text = data.refet
        frofileinfo(holder.userN,holder.status,holder.profileIm,data.UserId)
        findStared(data.blogId,holder.starbtn)
        starcount(data.blogId,holder.stars)
        holder.delete.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("SaveBlogs").child(firebaseUser!!.uid).child(data.blogId!!)
                .removeValue()
            holder.delete.visibility = View.GONE
        }
        holder.replay.setOnClickListener {
            val intent = Intent(context, ReplayActivity::class.java).apply {
                putExtra("BLOG_ID",blogId)
            }
            context.startActivity(intent)
        }
        holder.starbtn.setOnClickListener {
            if (holder.starbtn.tag.toString()=="star")
            {
                data.blogId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Stars").child(it1)
                        .child(firebaseUser!!.uid)
                        .setValue(true)
                }
            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("Stars").child(data.blogId!!)
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }
        }
    }



    private fun starcount(blogId: String?, stars: TextView?) {
        val postRef= FirebaseDatabase.getInstance().reference.child("Stars").child(blogId!!)
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            @SuppressLint("SetTextI18n")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                stars!!.text = datasnapshot.childrenCount.toString()+" Stars"
            }
        })
    }

    private fun findStared(blogId: String?, starbtn: ImageView?) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val postRef= FirebaseDatabase.getInstance().reference.child("Stars").child(blogId!!)
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(firebaseUser!!.uid).exists()) {
                    starbtn!!.setImageResource(R.drawable.round_star_24)
                    starbtn.tag = "stared"
                }
                else {
                    starbtn?.setImageResource(R.drawable.round_star_border_24)
                    starbtn?.tag = "star"
                }
            }
        })
    }
    private fun frofileinfo(
        userN: TextView?,
        status: TextView?,
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
                            Picasso.get().load(user.UserImageUrl).placeholder(R.drawable.profile).into(profileIm)
                        } else {
                            profileIm?.setImageResource(R.drawable.profile)
                        }
                    }
                    if (userN != null) {
                        if (user != null) {
                            userN.text =(user.UserName)
                        }
                    }

                    if (user != null) {
                        if (status != null) {
                            status.text =(user.UserStatus)
                        }
                    }

                }
            }
        })
    }
    inner class SaveHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blog= itemView.findViewById<TextView>(R.id.blogtv)
        val title= itemView.findViewById<TextView>(R.id.tvTitle)
        val userN= itemView.findViewById<TextView>(R.id.userName)
        val status= itemView.findViewById<TextView>(R.id.status)
        val profileIm= itemView.findViewById<CircleImageView>(R.id.userProfile)
        val starbtn = itemView.findViewById<ImageView>(R.id.icStar)
        val stars = itemView.findViewById<TextView>(R.id.starCount)
        val replay = itemView.findViewById<ImageView>(R.id.reply)
        val ref = itemView.findViewById<TextView>(R.id.refarence)
        val delete = itemView.findViewById<ImageView>(R.id.deleteBtn)
    }
}