package com.example.theconsciousness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.theconsciousness.Models.Blog
import com.example.theconsciousness.databinding.ActivityEditBlogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class EditBlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBlogBinding
    private var blogId :String = ""
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        blogId = intent.getStringExtra("blogId").toString()

        val databaseReference = FirebaseDatabase.getInstance().getReference("Blogs").child(blogId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blog = snapshot.getValue(Blog::class.java)
                if (blog != null) {
                    binding.editTitle.setText(blog.title)
                }
                if (blog != null) {
                    binding.editReff.setText(blog.refet)
                }
                if (blog != null) {
                    binding.editBlog.setText(blog.blog)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Save the edited blog
        binding.btnUp.setOnClickListener {
            val updatedTitle = binding.editTitle.text.toString().lowercase(Locale.ROOT)
            val updatedContent = binding.editBlog.text.toString().lowercase(Locale.ROOT)
            val updatedRefet = binding.editReff.text.toString()

            val postMap = HashMap<String, Any>()
            postMap["UserId"] = auth.currentUser!!.uid
            postMap["uplaodTime"] = System.currentTimeMillis()
            postMap["blogId"] = blogId
            postMap["title"] = updatedTitle
            postMap["blog"] = updatedContent
            postMap["refet"] = updatedRefet

            databaseReference.setValue(postMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Blog updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update blog", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}