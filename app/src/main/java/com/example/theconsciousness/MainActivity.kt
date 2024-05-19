package com.example.theconsciousness


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.theconsciousness.Fragments.*
import com.example.theconsciousness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(OccasionFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                //home id carry Occasion Fragment
                //and occasion id carry Home
                R.id.occasion -> replaceFragment(HomeFragment())
                R.id.home -> replaceFragment(OccasionFragment())

                R.id.event -> replaceFragment(EventsFragment())
                R.id.counter -> replaceFragment(CounterFragment())
                else ->{

                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}