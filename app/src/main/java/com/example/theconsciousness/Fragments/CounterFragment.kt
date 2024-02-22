package com.example.theconsciousness.Fragments



import android.content.Context
import android.graphics.Color
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.theconsciousness.Models.DataPoint
import com.example.theconsciousness.databinding.FragmentCounterBinding
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class CounterFragment : Fragment() {
    private lateinit var binding: FragmentCounterBinding
    private var count:Int=0
    private var fullcount:Int=0
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCounterBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        binding.countBtn.setOnClickListener {
            countfun()
            if (binding.switchBtn.isChecked){
                vibrate(requireView())
            }

        }
        binding.clearBtn.setOnClickListener {
            cleardata()
        }

       binding.saveCount.setOnClickListener {
           saveCountData()
        }
        retrivedata()
        binding.countBtnMinas.setOnClickListener {
            count--
            binding.tvCount.text = count.toString()

        }

        return binding.root
    }

    private fun vibrate(requireView: View) {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT>=26){
            vibrator.vibrate(VibrationEffect.createOneShot(100L,VibrationEffect.DEFAULT_AMPLITUDE))
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
                Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(requireContext(), "Count added", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to add count", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun cleardata() {
        count= 0
        binding.tvCount.text = count.toString()
        fullcount= 0
        binding.tvFull.text = fullcount.toString()

    }

    private fun countfun() {
        count++
        binding.tvCount.text = count.toString()
        if (count==108){
            count = 0
            fullmal()
        }
    }
    private fun fullmal() {
        fullcount++
        binding.tvFull.text = fullcount.toString()

    }

}