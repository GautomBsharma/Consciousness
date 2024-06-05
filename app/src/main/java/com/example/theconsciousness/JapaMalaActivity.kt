package com.example.theconsciousness

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import com.example.theconsciousness.Models.DataPoint
import com.example.theconsciousness.databinding.ActivityJapaMalaBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JapaMalaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJapaMalaBinding
    private var count:Int=0
    private var fullcount:Int=0
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJapaMalaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.countBtn.setOnClickListener {
            countfun()
            if (binding.switchBtn.isChecked){
                vibrate()
            }

        }
        binding.clearBtn.setOnClickListener {
            cleardata()
        }

        binding.saveCount.setOnClickListener {
            if (isNetworkAvailable(this)) {
                // Internet is available, retrieve data
                saveCountData()
            } else {
                // No internet connection, show dialog
                showNoInternetDialog()
            }

        }
        retrivedata()
        binding.countBtnMinas.setOnClickListener {
            if (count>=1){
                count--
                binding.tvShowCount.text = count.toString()

            }

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

    private fun vibrate() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT>=26){
            vibrator.vibrate(VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else{
            vibrator.vibrate(100L)
        }

    }
    private fun retrivedata() {

        val reference = FirebaseDatabase.getInstance().reference.child("Count").child(auth.currentUser!!.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val entries = ArrayList<Entry>()

                for (snapshot in dataSnapshot.children) {
                    val datapoint = snapshot.getValue(DataPoint::class.java)

                    if (datapoint != null) {
                        entries.add(Entry(datapoint.xValue, datapoint.yValue))
                    }
                }

                displayLineChart(entries)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@JapaMalaActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayLineChart(entries: ArrayList<Entry>) {
        if (entries.isNotEmpty()) {
            val dataSet = LineDataSet(entries, "Japa Mala Data")
            dataSet.color = Color.BLUE
            dataSet.setCircleColor(Color.RED)
            dataSet.lineWidth = 2f

            val lineData = LineData(dataSet)
            binding.lineChart.data = lineData
        } else {
            binding.lineChart.clear()
        }

        binding.lineChart.invalidate()
        binding.lineChart.description.isEnabled = false
    }

    private fun saveCountData() {
        val userId = auth.currentUser?.uid
        userId?.let {
            val ref = FirebaseDatabase.getInstance().reference.child("Count").child(userId)

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val xValue = snapshot.childrenCount.toFloat()
                    val yValue = fullcount.toFloat()
                    val barChartData = DataPoint(xValue, yValue)

                    ref.push().setValue(barChartData)
                        .addOnSuccessListener {
                            Toast.makeText(this@JapaMalaActivity, "Count added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@JapaMalaActivity, "Failed to add count", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@JapaMalaActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun cleardata() {
        count= 0
        binding.tvShowCount.text = count.toString()
        fullcount= 0
        binding.tvTotal.text = "Total : $fullcount"

    }

    private fun countfun() {
        count++
        binding.tvShowCount.text = count.toString()
        if (count==108){
            count = 0
            fullmal()
        }
    }
    private fun fullmal() {
        fullcount++
        binding.tvTotal.text = "Total : $fullcount"

    }
}