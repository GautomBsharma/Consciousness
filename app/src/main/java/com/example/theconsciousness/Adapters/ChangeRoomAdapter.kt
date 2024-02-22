package com.example.theconsciousness.Adapters


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

class ChangeRoomAdapter(val context: Context, val guruList: ArrayList<User>): RecyclerView.Adapter<ChangeRoomAdapter.ChangeRoomHolder>() {
    private var firebaseUser: FirebaseUser?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeRoomHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.change_room_item,parent,false)
        return ChangeRoomHolder(view)
    }

    override fun getItemCount(): Int {
        return guruList.size
    }

    override fun onBindViewHolder(holder: ChangeRoomHolder, position: Int) {
        val data = guruList[position]
        holder.userN.text = data.UserName
        holder.bio.text = data.UserBio
        holder.ustatus.text = data.UserStatus
        val gusrid = data.UserId
        getIsJoined(holder.setGuru,data.UserId)

        if (data.UserImageUrl.isNotEmpty()){
            Picasso.get().load(data.UserImageUrl).placeholder(R.drawable.profile).into(holder.profileIm)
        }

        holder.setGuru.setOnClickListener {


            if (holder.setGuru.tag.toString()=="Join")
            {
                 FirebaseDatabase.getInstance().reference.child("CurrentRoom").child(firebaseUser!!.uid)
                    .child(gusrid)
                    .setValue(true)


            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("CurrentRoom").child(firebaseUser!!.uid)
                    .child(gusrid)
                    .removeValue()
            }


        }
    }


    private fun getIsJoined(guru: TextView?, userId: String) {
        firebaseUser= FirebaseAuth.getInstance().currentUser
        val postRef= FirebaseDatabase.getInstance().reference.child("CurrentRoom").child(firebaseUser!!.uid)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(userId).exists()) {
                    if (guru != null) {
                        guru.text = "Joined"
                    }
                    if (guru != null) {
                        guru.tag = "Joined"
                    }
                }
                else {
                    guru?.text = "Join in Room"
                    guru?.tag = "Join"
                }
            }
        })
    }



    inner class ChangeRoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val userN= itemView.findViewById<TextView>(R.id.userName)
        val ustatus= itemView.findViewById<TextView>(R.id.uStatus)
        val bio= itemView.findViewById<TextView>(R.id.tvBio)
        val profileIm= itemView.findViewById<CircleImageView>(R.id.guruPImage)
        val setGuru = itemView.findViewById<TextView>(R.id.setjoin)

    }
}