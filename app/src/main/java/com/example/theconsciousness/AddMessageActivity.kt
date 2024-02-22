package com.example.theconsciousness

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.theconsciousness.databinding.ActivityAddMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlin.collections.HashMap
import kotlin.random.Random

class AddMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMessageBinding
    private lateinit var auth: FirebaseAuth
    private var userId : String=""
    private lateinit var context :Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid.toString()
        binding.withNoti.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.withoutNot.isChecked = false
            }
        }
        binding.withoutNot.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.withNoti.isChecked = false
            }
        }


        binding.btnUp.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.edMessage.text.toString().isEmpty()){
            binding.edMessage.error = "Enter message"
        }
        else{
            savedata()
        }
    }

    private fun savedata() {
        if (binding.withNoti.isChecked && binding.edMessage.text.toString().isNotEmpty()){
            saveWithNotifi()

        }
        else if (binding.withoutNot.isChecked && binding.edMessage.text.toString().isNotEmpty()){
            saveWithoutNotifi()
        }
        else{
            Toast.makeText(
                this,
                "Write message and select message type also ",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun saveWithoutNotifi() {

        val refff= FirebaseDatabase.getInstance().reference.child("ScholarPostRoom").child(userId!!)
        val MessageMap = HashMap<String,Any>()
        val postId= refff.push().key
        MessageMap["scholarpost"] = binding.edMessage.toString()
        MessageMap["uplaodTime"] = System.currentTimeMillis()
        MessageMap["UserId"] = userId
        MessageMap["scholarpostId"] = postId.toString()

        refff.push().setValue(MessageMap).addOnSuccessListener {

            Toast.makeText(this, "Send successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this, " Failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun saveWithNotifi() {

        val refff= FirebaseDatabase.getInstance().reference.child("ScholarPostRoom").child(userId!!)
        val MessageMap = HashMap<String,Any>()
        val postId= refff.push().key
        MessageMap["scholarpost"] = binding.edMessage.toString()
        MessageMap["uplaodTime"] = System.currentTimeMillis()
        MessageMap["UserId"] = userId
        MessageMap["scholarpostId"] = postId.toString()
        refff.push().setValue(MessageMap).addOnSuccessListener {
            sendNotification()
            Toast.makeText(this, "Send successfully", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this, " Failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun sendNotification() {
        val disciplesRef = FirebaseDatabase.getInstance().getReference("Disciple").child(userId)

        disciplesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (discipleSnapshot in dataSnapshot.children) {
                    val discipleId = discipleSnapshot.key

                    // Send notification to each disciple in the group using FCM
                    if (discipleId != null) {
                        sendNotificationToDisciple(discipleId, "New message from your Guru!",context)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

     private fun sendNotificationToDisciple(discipleId: String, message: String, context: Context) {
        val channelName = "Scholar Notifications"
        val channelId = "${context.packageName}.scholar_notifications"
        val disciplesRef = FirebaseDatabase.getInstance().getReference("Users").child(discipleId)
        disciplesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val fcmToken = dataSnapshot.child("UserToken").getValue(String::class.java)
                //val  fcmToken= dataSnapshot.getValue(String::class.java)

                if (!fcmToken.isNullOrEmpty()) {
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val existingChannel = notificationManager.getNotificationChannel(channelId)

                        if (existingChannel == null) {
                            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                            notificationManager.createNotificationChannel(channel)
                        }
                    }
                    val notificationBuilder = NotificationCompat.Builder(context, channelId)
                        .setContentTitle("Scholar message")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.logocon)
                        .setAutoCancel(true)
                    val notificationId = System.currentTimeMillis().toInt() + Random.nextInt(0, 1000)
                    val notification = notificationBuilder.build()
                    notificationManager.notify(notificationId, notification)
                } else {
                    println("FCM token not found for disciple $discipleId")
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Database error: $databaseError")
            }
        })
    }

}