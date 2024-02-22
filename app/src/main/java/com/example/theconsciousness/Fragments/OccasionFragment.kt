package com.example.theconsciousness.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.*
import com.example.theconsciousness.Adapters.BlogAdapter
import com.example.theconsciousness.Models.Blog
import com.example.theconsciousness.databinding.FragmentOccasionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OccasionFragment : Fragment() {
    private lateinit var binding: FragmentOccasionBinding
    private var adapter:BlogAdapter?=null
    private var blogList:ArrayList<Blog>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentOccasionBinding.inflate(layoutInflater)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blogb)
        val linearlayoutManager= LinearLayoutManager(context)
        linearlayoutManager.reverseLayout=true
        linearlayoutManager.stackFromEnd=true
        binding.recyclebook.layoutManager=linearlayoutManager
        blogList=ArrayList()
        adapter=context?.let { BlogAdapter(it,blogList as ArrayList<Blog>) }
        binding.recyclebook.adapter=adapter
        binding.writecon.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }
        binding.guruCard.setOnClickListener {
            startActivity(Intent(requireContext(),ScholarActivity::class.java))
        }

        binding.favconsta.setOnClickListener {
            startActivity(Intent(requireContext(),FevoriteActivity::class.java))
        }
        binding.searchGro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchGro.text.toString() == "")
                {

                }
                else {
                    searchUser(s.toString().lowercase())
                }
            }
        })
        retrivebBlog()
        return binding.root
    }
    private fun retrivebBlog() {
        val blgRef = FirebaseDatabase.getInstance().reference.child("Blogs")
        blgRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    blogList!!.clear()
                    for (snap in snapshot.children){
                        val rent = snap.getValue(Blog::class.java)
                        blogList!!.add(rent!!)
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun searchUser(input:String) {
        val query= FirebaseDatabase.getInstance().reference
            .child("Blogs")
            .orderByChild("title")
            .startAt(input)
            .endAt(input + "\uf8ff")
        query.addValueEventListener(object: ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(datasnapshot: DataSnapshot) {
                blogList?.clear()
                for(snapshot in datasnapshot.children)
                {
                    val user=snapshot.getValue(Blog::class.java)
                    if(user!=null)
                    {
                        blogList?.add(user)
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }
}