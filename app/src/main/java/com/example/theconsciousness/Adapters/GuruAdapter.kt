package com.example.theconsciousness.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GuruAdapter(val context: Context, val guruList: ArrayList<User>): RecyclerView.Adapter<GuruAdapter.GuruHolder>() {
    private var firebaseUser: FirebaseUser?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuruHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.guru_item,parent,false)
        return GuruHolder(view)
    }

    override fun getItemCount(): Int {
        return guruList.size
    }

    override fun onBindViewHolder(holder: GuruHolder, position: Int) {
        val data = guruList[position]
        holder.userN.text = data.UserName
        holder.bio.text = data.UserBio
        holder.ustatus.text = data.UserStatus
        getIsGuru(holder.setGuru,data.UserId)
        getCount(holder.disCount,data.UserId)
        if (data.UserImageUrl.isNotEmpty()){
            Picasso.get().load(data.UserImageUrl).placeholder(R.drawable.profile).into(holder.profileIm)
        }

        holder.setGuru.setOnClickListener {


            if (holder.setGuru.tag.toString()=="Guru")
            {
                data.UserId.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Disciple").child(it1)
                        .child(firebaseUser!!.uid)
                        .setValue(true)
                }.addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("MyGuru").child(firebaseUser!!.uid)
                        .child(data.UserId)
                        .setValue(true)
                }.addOnSuccessListener {

                    saveCurrent(data.UserId)
                }


            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("Disciple").child(data.UserId)
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }


        }
    }


    private fun saveCurrent(userId: String) {
       val ref = FirebaseDatabase.getInstance().reference.child("CurrentRoom").child(firebaseUser!!.uid)
        val CurrentMap = HashMap<String,Any>()
        CurrentMap["CurrentRoomId"] = userId
        ref.setValue(CurrentMap)

    }

    private fun getIsGuru(guru: TextView?, userId: String) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val postRef=FirebaseDatabase.getInstance().reference.child("Disciple").child(userId!!)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(firebaseUser!!.uid).exists()) {
                    if (guru != null) {
                        guru.text = "My Guru"
                    }
                    if (guru != null) {
                        guru.tag = "myGuru"
                    }
                }
                else {
                    guru?.text = "Join in Room as Disciple"
                    guru?.tag = "Guru"
                }
            }
        })
    }

    private fun getCount(disCount: TextView, userId: String) {
        val postRef=FirebaseDatabase.getInstance().reference.child("Disciple").child(userId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                disCount.text = datasnapshot.childrenCount.toString()+" Disciples"
            }
        })

    }

    inner class GuruHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

        val userN= itemView.findViewById<TextView>(R.id.userName)
        val ustatus= itemView.findViewById<TextView>(R.id.uStatus)
        val bio= itemView.findViewById<TextView>(R.id.tvBio)
        val profileIm= itemView.findViewById<CircleImageView>(R.id.guruPImage)
        val disCount = itemView.findViewById<TextView>(R.id.tvDiscipleCo)
        val setGuru = itemView.findViewById<TextView>(R.id.setGuru)

    }
}