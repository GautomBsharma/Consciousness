package com.example.theconsciousness.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.theconsciousness.LoginActivity
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.R

import com.example.theconsciousness.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        binding.logoutCar.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        getprofileData()
        return binding.root
    }

    private fun getprofileData() {
        val reff = FirebaseDatabase.getInstance().reference.child("Users")
        reff.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(User::class.java)
                    if (data?.UserId == auth.currentUser?.uid){
                        if (data != null) {
                            binding.userBio.text = data.UserBio
                        }
                        binding.userName.text = data?.UserName
                        binding.pEmail.text = data?.UserEmail
                        if (data != null) {
                            if(data.UserDistrict.isEmpty()){
                                binding.pAdress.text = "Edit Your Address"
                            }
                            else{
                                binding.pAdress.text = data.UserDistrict
                            }
                        }
                        if (data != null) {
                            if (data.UserInstitute.isEmpty()){
                                binding.tempNmae.text = "Edit your current and loving Temple name"
                            }
                            else{
                                binding.tempNmae.text = data.UserInstitute
                            }

                        }
                        if (data != null) {
                            if (data.UserImageUrl.isNotEmpty()) {
                                Picasso.get().load(data.UserImageUrl).placeholder(R.drawable.my_profile).into(binding.profileImage)
                            } else {
                                binding.profileImage.setImageResource(R.drawable.my_profile)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}