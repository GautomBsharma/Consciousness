package com.example.theconsciousness

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.theconsciousness.Models.User
import com.example.theconsciousness.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.logoutCar.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.editPro.setOnClickListener {
            startActivity(Intent(this,EditActivity::class.java))
        }
        binding.goPrayerPost.setOnClickListener {
            startActivity(Intent(this, MyPrayerActivity::class.java))
        }
        binding.goBlog.setOnClickListener {
            startActivity(Intent(this, MyBlogActivity::class.java))
        }


        if (isNetworkAvailable(this)) {
            // Internet is available, retrieve data
            getprofileData()
        } else {
            // No internet connection, show dialog
            showNoInternetDialog()
        }



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
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
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
    private fun getprofileData() {
        auth.currentUser?.let {
            FirebaseDatabase.getInstance().reference.child("Users").child(
                it.uid
            )
        }?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(User::class.java)
                    if (data?.UserId == auth.currentUser?.uid) {
                        if (data != null) {
                            binding.userBio.text = data.UserBio
                        }

                        binding.userName.text = data?.UserName
                        binding.pEmail.text = data?.UserEmail
                        if (data != null) {
                            if (data.UserDistrict.isEmpty()) {
                                binding.pAdress.text = "Edit Your Address"
                            } else {
                                binding.pAdress.text = data.UserDistrict
                            }
                        }
                        if (data != null) {
                            binding.tvStatus.text = data.UserStatus
                        }

                        /*if (data != null) {
                            if (data.UserInstitute.isEmpty()) {
                                binding.tempNmae.text = "Edit your current and loving Temple name"
                            } else {
                                binding.tempNmae.text = data.UserInstitute
                            }

                        }*/
                        if (data != null) {
                            if (data.UserImageUrl.isNotEmpty()) {
                                Picasso.get().load(data.UserImageUrl)
                                    .placeholder(R.drawable.my_profile).into(binding.profileImage)
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