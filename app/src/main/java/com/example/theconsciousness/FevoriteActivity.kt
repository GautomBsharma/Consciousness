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
                    binding.textView20.visibility = View.VISIBLE
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

        for (blogId in blogIds) {
            //val blogRef = blogsRef.child(blogId)
            blogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(blogSnapshot: DataSnapshot) {

                    for (snap in blogSnapshot.children){
                        val data = snap.getValue(Blog::class.java)
                        if (blogId == data?.blogId){
                            fetchedBlogs.add(data)
                        }
                    }
                    /*val dat = blogSnapshot.getValue(Blog::class.java)

                    val blogDetails: Map<String, Any>? = blogSnapshot.value as? Map<String, Any>
                    // Extract and create Blog object
                    blogDetails?.let {
                        val blog = Blog(
                            blogId,
                            it["UserId"].toString(),
                            it["title"].toString(),
                            it["blog"].toString(),
                            it["refet"].toString(),
                            it["uplaodTime"] as Long // Adjust this based on your data structure
                        )
                        fetchedBlogs.add(blog)

                        if (fetchedBlogs.size == blogIds.size) {
                            val adapter = SaveAdapter(this@FevoriteActivity, fetchedBlogs)
                            binding.recyclerViewSave.adapter = adapter
                            binding.recyclerViewSave.layoutManager = LinearLayoutManager(this@FevoriteActivity)
                        }
                    }*/
                    val adapter = SaveAdapter(this@FevoriteActivity, fetchedBlogs)
                    binding.recyclerViewSave.adapter = adapter
                    binding.recyclerViewSave.layoutManager = LinearLayoutManager(this@FevoriteActivity)
                }
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

    }
}