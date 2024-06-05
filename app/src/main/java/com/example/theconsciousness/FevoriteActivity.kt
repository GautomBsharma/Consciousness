package com.example.theconsciousness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.SaveAdapter
import com.example.theconsciousness.Models.Blog
import com.example.theconsciousness.databinding.ActivityFevoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FevoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFevoriteBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFevoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val savedBlogsRef =
            auth.currentUser?.let {
                FirebaseDatabase.getInstance().reference.child("SaveBlogs").child(
                    it.uid)
            }
        savedBlogsRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blogIds = mutableListOf<String>()

                for (childSnapshot in snapshot.children) {
                    val blogId = childSnapshot.key
                    blogId?.let { blogIds.add(it) }
                }
                if (blogIds.isEmpty()){
                    binding.tvIsSave.visibility = View.VISIBLE
                }
                else{
                    fetchBlogDetails(blogIds)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

    }

    private fun fetchBlogDetails(blogIds: List<String>) {
        val blogsRef = FirebaseDatabase.getInstance().reference.child("Blogs")
        val fetchedBlogs = mutableListOf<Blog>()

        blogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(blogSnapshot: DataSnapshot) {
                for (blogId in blogIds) {
                    val blogSnapshot = blogSnapshot.child(blogId)
                    val data = blogSnapshot.getValue(Blog::class.java)
                    data?.let {
                        fetchedBlogs.add(it)
                    }
                }
                if (fetchedBlogs.isEmpty()) {
                    binding.tvIsSave.visibility = View.VISIBLE
                } else {
                    val adapter = SaveAdapter(this@FevoriteActivity, fetchedBlogs)
                    binding.recyclerViewSave.adapter = adapter
                    binding.recyclerViewSave.layoutManager = LinearLayoutManager(this@FevoriteActivity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}