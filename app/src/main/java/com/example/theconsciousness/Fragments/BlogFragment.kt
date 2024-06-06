package com.example.theconsciousness.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theconsciousness.Adapters.BlogAdapter
import com.example.theconsciousness.AddBookActivity
import com.example.theconsciousness.FevoriteActivity
import com.example.theconsciousness.Models.Blog
import com.example.theconsciousness.R
import com.example.theconsciousness.databinding.FragmentBlogBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BlogFragment : Fragment() {
    private lateinit var binding: FragmentBlogBinding
    private var adapter: BlogAdapter?=null
    private var blogList:ArrayList<Blog>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBlogBinding.inflate(layoutInflater)

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
        binding.saved.setOnClickListener {
            startActivity(Intent(requireContext(), FevoriteActivity::class.java))
        }
        binding.searchGro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.searchGro.text.toString().isEmpty())
                {
                    retrivebBlog()
                }
                else {
                    searchUser(s.toString().lowercase())
                }
            }
        })

        if (isNetworkAvailable(requireContext())) {
            // Internet is available, retrieve data
            retrivebBlog()
        } else {
            // No internet connection, show dialog
            showNoInternetDialog()
        }

        return binding.root
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun showNoInternetDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("No Internet Connection")
            .setIcon(R.drawable.round_signal_wifi_connected_no_internet_4_24)
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.show()
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