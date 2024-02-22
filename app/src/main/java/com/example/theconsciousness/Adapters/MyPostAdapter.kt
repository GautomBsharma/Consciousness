package com.example.theconsciousness.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.theconsciousness.Models.PrayerPost
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class MyPostAdapter(private var context: Context,private var datalist:ArrayList<PrayerPost>):RecyclerView.Adapter<MyPostAdapter.PrayHolder>(){

    private var firebaseUser: FirebaseUser?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pray_item,parent,false)
        return PrayHolder(view)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: PrayHolder, position: Int) {
        val data = datalist[position]

        holder.ptitle.text = data.post
        if (data.prayerImageUrl?.isNotEmpty() == true){
            holder.prayerIm.visibility = View.VISIBLE
            Glide.with(context).load(data.prayerImageUrl).into(holder.prayerIm)
        }
        data.UserId?.let { userData(holder.userN,holder.profilepr,holder.pstatus, it) }
        data.postId?.let { isFlowered(it,holder.flowerbtn) }
        data.postId?.let { getCountofFlowered(it,holder.flowerCount) }
        data.postId?.let { isPrayered(it,holder.prayerbtn) }
        data.postId?.let { getCountofFrayred(it,holder.prayCoun) }



        holder.flowerbtn.setOnClickListener{
            if (holder.flowerbtn.tag.toString()=="flower")
            {
                data.postId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Flowers").child(it1)
                        .child(firebaseUser!!.uid)
                        .setValue(true)
                }

            }
            else
            {
                data.postId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Flowers").child(it1)
                        .child(firebaseUser!!.uid)
                        .removeValue()
                }
            }
        }
        holder.prayerbtn.setOnClickListener {
            if (holder.prayerbtn.tag.toString()=="prayer")
            {
                data.postId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Prayers").child(it1)
                        .child(firebaseUser!!.uid)
                        .setValue(true)
                }

            }
            else
            {
                data.postId?.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Prayers").child(it1)
                        .child(firebaseUser!!.uid)
                        .removeValue()
                }
            }

        }


    }

    private fun isPrayered(postid:String,imlove: ImageView) {

        firebaseUser= FirebaseAuth.getInstance().currentUser
        val postRef= FirebaseDatabase.getInstance().reference.child("Prayers").child(postid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (firebaseUser?.let { datasnapshot.child(it.uid).exists() } == true) {
                    imlove.setImageResource(R.drawable.prayed)
                    imlove.tag = "prayered"
                }
                else {
                    imlove.setImageResource(R.drawable.pray)
                    imlove.tag = "prayer"
                }
            }
        })
    }
    private fun isFlowered(postid:String, imlove: ImageView) {

        firebaseUser= FirebaseAuth.getInstance().currentUser
        val postRef= FirebaseDatabase.getInstance().reference.child("Flowers").child(postid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (firebaseUser?.let { datasnapshot.child(it.uid).exists() } == true) {
                    imlove.setImageResource(R.drawable.lotused)
                    imlove.tag = "flowered"
                }
                else {
                    imlove.setImageResource(R.drawable.lotus)
                    imlove.tag = "flower"
                }
            }
        })
    }

    private fun getCountofFlowered(postid:String,likesNo: TextView) {

        val postRef= FirebaseDatabase.getInstance().reference.child("Flowers").child(postid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                likesNo.text = datasnapshot.childrenCount.toString()+" Flowers"
            }
        })
    }
    private fun getCountofFrayred(postid:String,likesNo: TextView) {

        val postRef= FirebaseDatabase.getInstance().reference.child("Prayers").child(postid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                likesNo.text = datasnapshot.childrenCount.toString()+" Prayers"
            }
        })
    }


    private fun userData(
        name: TextView?,
        pimage: CircleImageView?,
        pstatus: TextView,
        userId: String

    ) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        if (pimage != null) {
                            Glide.with(context).load(user.UserImageUrl).into(pimage)
                        }
                    }
                    name!!.text =(user!!.UserName)
                    pstatus.text =(user.UserStatus)

                }

            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    inner class PrayHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val prayerIm= itemView.findViewById<ImageView>(R.id.prayerImage)
        val ptitle= itemView.findViewById<TextView>(R.id.tvdescr)
        val userN= itemView.findViewById<TextView>(R.id.userpName)
        val pstatus= itemView.findViewById<TextView>(R.id.statusp)
        val profilepr= itemView.findViewById<CircleImageView>(R.id.userpProfile)
        val prayerbtn = itemView.findViewById<ImageView>(R.id.prayerIm)
        val flowerbtn = itemView.findViewById<ImageView>(R.id.flowered)
        val prayCoun = itemView.findViewById<TextView>(R.id.prayCount)
        val flowerCount = itemView.findViewById<TextView>(R.id.flowerCoun)
        val moreee = itemView.findViewById<ImageView>(R.id.more)
    }

}