package com.example.theconsciousness

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.theconsciousness.DataHelperClasess.DataHelperMaster
import com.example.theconsciousness.DataHelperClasess.DataHelperPorn
import com.example.theconsciousness.DataHelperClasess.DatabaseHelper
import com.example.theconsciousness.Models.Note
import com.example.theconsciousness.databinding.ActivityNegativityBinding
import java.text.SimpleDateFormat
import java.util.*

class NegativityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNegativityBinding
    private lateinit var masterPref: SharedPreferences
    private lateinit var pornPref: SharedPreferences
    lateinit var dataHelper: DataHelperMaster

    lateinit var dataHelperPorn: DataHelperPorn
    private lateinit var db: DatabaseHelper
    private val timer = Timer()
    private val timerPorn = Timer()
    private var countMast = 1
    private val handler = Handler()
    private var countPorn = 1
    private val handlerPorn = Handler()
    private var timeCountMaster = false
    private var timeCountPorn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNegativityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        masterPref = getSharedPreferences("MyPrefsMaster", Context.MODE_PRIVATE)
        pornPref = getSharedPreferences("MyPrefsPorn", Context.MODE_PRIVATE)

        dataHelper = DataHelperMaster(applicationContext)
        dataHelperPorn = DataHelperPorn(applicationContext)

        binding.goReason.setOnClickListener {
            startActivity(Intent(this,ReasonActivity::class.java))
        }

        binding.setMaster.setOnClickListener { startStopAction()
            getTimeStatusMaster()
            knowStatMaster()
        }
        binding.resetMaster.setOnClickListener { resetAction() }
        binding.setPorn.setOnClickListener { startStopActionPorn()
            getTimeStatusPor()
            knowStatPor()
        }
        binding.resetPorn.setOnClickListener { resetPornAction() }


        getTimeStatusMaster()

        if (timeCountMaster){
            knowStatMaster()
        }


        getTimeStatusPor()

        if (timeCountPorn){
            knowStatPor()
        }

        rdpornAddiction()


        if (dataHelper.timerCounting()) {
            startTimer()
        } else {
            stopTimer()
            if (dataHelper.startTime() != null && dataHelper.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                binding.tvShowtimeMaster.text = timeStringFromLong(time)
                //updateTimeProgress(time)
            }
        }


        timer.scheduleAtFixedRate(TimeTask(), 0, 500)


    }

    private fun getTimeStatusMaster() : Boolean {

        timeCountMaster = dataHelper.timerCounting()
        return timeCountMaster
    }

    private fun knowStatMaster() {
        if (timeCountMaster){
            masterCountProg()
        }
        else{
            countMast = 1
            binding.progressBarMaster.progress = countMast
        }
    }
    private fun getTimeStatusPor() : Boolean {

        timeCountPorn = dataHelperPorn.timerCounting()
        return timeCountPorn
    }
    private fun knowStatPor() {
        if (timeCountPorn){
            pornCountProg()
        }
        else{
            countPorn = 2
            binding.progressBarPorn.progress = countPorn
        }
    }
    private fun masterCountProg() {
        handler.post(object : Runnable {
            override fun run() {
                binding.progressBarMaster.progress = countMast
                if (countMast <= 100) {
                    countMast++
                    handler.postDelayed(this, 1000) // Update every second
                } else {
                    countMast = 1
                    // Restart the animation after reaching 100
                    handler.post(this)
                }
            }
        })
    }
    private fun stopHandlerMaster() {
        handler.removeCallbacksAndMessages(null)
        binding.progressBarMaster.progress =3
    }

    private fun pornCountProg() {
        handlerPorn.post(object : Runnable {
            override fun run() {
                binding.progressBarPorn.progress = countPorn
                if (countPorn <= 100) {
                    countPorn++
                    handlerPorn.postDelayed(this, 1000) // Update every second
                } else {
                    countPorn = 1
                    // Restart the animation after reaching 100
                    handlerPorn.post(this)
                }
            }
        })
    }

    private fun stopHandlerPorn() {
        handlerPorn.removeCallbacksAndMessages(null)
        binding.progressBarPorn.progress =3
    }


    private fun rdpornAddiction() {
        if (dataHelperPorn.timerCounting()) {
            startTimerPorn()
        } else {
            stopTimerPorn()
            if (dataHelperPorn.startTime() != null && dataHelperPorn.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                binding.tvShowtimeporn.text = timeStringFromLongPorn(time)

            }
        }

        timerPorn.scheduleAtFixedRate(TimeTaskPorn(), 0, 500)
    }

    private inner class TimeTaskPorn : TimerTask() {
        override fun run() {
            if (dataHelperPorn.timerCounting()) {
                val time = Date().time - dataHelperPorn.startTime()!!.time
                runOnUiThread {
                    binding.tvShowtimeporn.text = timeStringFromLongPorn(time)

                }
            }
        }
    }
    private inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting()) {
                val time = Date().time - dataHelper.startTime()!!.time
                runOnUiThread {
                    binding.tvShowtimeMaster.text = timeStringFromLong(time)

                }
            }
        }
    }
    @SuppressLint("MissingInflatedId")
    private fun resetPornAction() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_reset, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle("Reset and Add Note")

        dialogBuilder.setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
            // Validation and insertion code
            val editText = dialogView.findViewById<EditText>(R.id.additionalText)
            val additionalText = editText.text.toString()
            if (additionalText.isNotEmpty()) {
                val time = System.currentTimeMillis() // Current time
                val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(time))
                val eventType = "Quit Porn"
                // Inserting data into SQLite
                insertData(eventType,formattedTime, additionalText)
            }
            //resetTimer()
            // Your existing reset function
            timeCountPorn = false
            //knowStat()
            stopHandlerPorn()
            countPorn = 3
            binding.progressBarPorn.progress = countPorn

        })

        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
            timeCountPorn = false
            stopHandlerPorn()
            countPorn = 3
            binding.progressBarPorn.progress = countPorn

        })

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        dataHelperPorn.setStopTime(null)
        dataHelperPorn.setStartTime(null)
        stopTimerPorn()
        binding.tvShowtimeporn.text = timeStringFromLongPorn(0)
    }
    @SuppressLint("MissingInflatedId")
    private fun resetAction() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_reset, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle("Reset and Add Note")

        dialogBuilder.setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
            // Validation and insertion code
            val editText = dialogView.findViewById<EditText>(R.id.additionalText)
            val additionalText = editText.text.toString()
            if (additionalText.isNotEmpty()) {
                val time = System.currentTimeMillis() // Current time
                val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(time))
                val eventType = "Quit Masturbation"
                // Inserting data into SQLite
                insertData(eventType,formattedTime, additionalText)
                //stopTimer()
                // Your existing reset function
                //dataHelper.setTimerCounting(false)

                timeCountMaster = false
                //knowStat()
                stopHandlerMaster()
                countMast = 3
                binding.progressBarMaster.progress = countMast
            }
        })

        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
            timeCountMaster = false
            stopHandlerMaster()
            //knowStat()
        })

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.tvShowtimeMaster.text = timeStringFromLong(0)
    }

    private fun stopTimer() {
        dataHelper.setTimerCounting(false)

        binding.setMaster.visibility = View.VISIBLE
        binding.resetMaster.visibility = View.GONE
    }
    private fun stopTimerPorn() {
        dataHelperPorn.setTimerCounting(false)

        binding.setPorn.visibility = View.VISIBLE
        binding.resetPorn.visibility = View.GONE
    }


    private fun startTimer() {
        dataHelper.setTimerCounting(true)

        binding.setMaster.visibility = View.GONE
        binding.resetMaster.visibility = View.VISIBLE
    }
    private fun startTimerPorn() {
        dataHelperPorn.setTimerCounting(true)

        binding.setPorn.visibility = View.GONE
        binding.resetPorn.visibility = View.VISIBLE
    }

    private fun startStopAction() {
        if (dataHelper.timerCounting()) {
            dataHelper.setStopTime(Date())
            stopTimer()
        } else {
            if (dataHelper.stopTime() != null) {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            } else {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }
    private fun startStopActionPorn() {
        if (dataHelperPorn.timerCounting()) {
            dataHelperPorn.setStopTime(Date())
            stopTimerPorn()
        } else {
            if (dataHelperPorn.stopTime() != null) {
                dataHelperPorn.setStartTime(calcRestartTimePorn())
                dataHelperPorn.setStopTime(null)
            } else {
                dataHelperPorn.setStartTime(Date())
            }
            startTimerPorn()
        }
    }

    private fun calcRestartTime(): Date {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }

    private fun calcRestartTimePorn(): Date {
        val diff = dataHelperPorn.startTime()!!.time - dataHelperPorn.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }
    private fun timeStringFromLong(ms: Long): String {
        val days = ms / (1000 * 60 * 60 * 24)
        val hours = (ms / (1000 * 60 * 60) % 24)
        val minutes = (ms / (1000 * 60) % 60)
        val seconds = (ms / 1000) % 60
        return makeTimeString(days, hours, minutes, seconds)
    }

    private fun makeTimeString(days: Long, hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d d %02d h %02d m %02d s", days, hours, minutes, seconds)
    }
    private fun timeStringFromLongPorn(ms: Long): String {
        val days = ms / (1000 * 60 * 60 * 24)
        val hours = (ms / (1000 * 60 * 60) % 24)
        val minutes = (ms / (1000 * 60) % 60)
        val seconds = (ms / 1000) % 60
        return makeTimeStringPorn(days, hours, minutes, seconds)
    }

    private fun makeTimeStringPorn(days: Long, hours: Long, minutes: Long, seconds: Long): String {
        return String.format("%02d d %02d h %02d m %02d s", days, hours, minutes, seconds)
    }

    private fun insertData(eventNam : String,time: String, additionalText: String) {
        // Insert data into SQLite database
        val note = Note(0,eventNam, time, additionalText)
        db.insertN(note)
        Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show()
    }
}