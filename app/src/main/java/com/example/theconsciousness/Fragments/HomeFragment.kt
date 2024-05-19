package com.example.theconsciousness.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.theconsciousness.Adapters.MyPostAdapter
import com.example.theconsciousness.AddBlogActivity
import com.example.theconsciousness.Models.PrayerPost
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.ProfileActivity
import com.example.theconsciousness.R
import com.example.theconsciousness.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var adapter: MyPostAdapter ?= null
    private lateinit var auth: FirebaseAuth
    private var datalist :MutableList<PrayerPost>?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val linearlayoutManager= LinearLayoutManager(context)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true
        auth = FirebaseAuth.getInstance()
        binding.recycleHome.layoutManager=linearlayoutManager
        datalist=ArrayList()
        adapter=context?.let { MyPostAdapter(it,datalist as ArrayList<PrayerPost>) }
        binding.recycleHome.adapter=adapter
        binding.addBlog.setOnClickListener {
            startActivity(Intent(requireContext(),AddBlogActivity::class.java))
        }
        binding.userpProfile.setOnClickListener {
            startActivity(Intent(requireContext(),ProfileActivity::class.java))
        }
        getprofileImage()

        retriveBlog()
        return binding.root
    }

    private fun getprofileImage() {
        val blgRef =
            auth.currentUser?.uid?.let {
                FirebaseDatabase.getInstance().reference.child("Users").child(
                    it
                )
            }
        blgRef?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val rent = snapshot.getValue(User::class.java)
                    if (rent != null) {
                        if (rent.UserImageUrl.isNotEmpty()){
                            Glide.with(requireContext()).load(rent.UserImageUrl).into(binding.userpProfile)
                        }
                        else{
                            binding.userpProfile.setImageResource(R.drawable.my_profile)
                        }

                    }
                }
                }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

    private fun retriveBlog() {
        val blgRef =FirebaseDatabase.getInstance().reference.child("posts")
        blgRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    datalist!!.clear()
                    for (snap in snapshot.children){
                        val rent = snap.getValue(PrayerPost::class.java)
                        datalist!!.add(rent!!)

                    }
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}