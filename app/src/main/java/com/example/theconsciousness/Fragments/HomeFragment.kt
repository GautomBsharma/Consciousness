package com.example.theconsciousness.Fragments


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.theconsciousness.*
import com.example.theconsciousness.Models.Sloka
import com.example.theconsciousness.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlin.random.Random


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth :FirebaseAuth

    private var lastScrollY = 0
    private lateinit var database: FirebaseDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.idMala.setOnClickListener {
            startActivity(Intent(requireContext(),JapaMalaActivity::class.java))
        }
        binding.idtemple.setOnClickListener {
            startActivity(Intent(requireContext(),TempleActivity::class.java))
        }
        binding.idEvent.setOnClickListener {
            startActivity(Intent(requireContext(),EventActivity::class.java))
        }
        binding.imNegativity.setOnClickListener {
            startActivity(Intent(requireContext(),NegativityActivity::class.java))
        }
        binding.addSloka.setOnClickListener {
            startActivity(Intent(requireContext(),AddSlokaActivity::class.java))
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {


                R.id.nav_profile -> {
                    startActivity(Intent(requireContext(),ProfileActivity::class.java))
                    true
                }

                R.id.logout -> {
                    auth.signOut()
                    startActivity(Intent(requireContext(),LoginActivity::class.java))
                    true
                }
                // Add more cases for other menu items if needed
                else -> false
            }
        }
        binding.appManu.setOnClickListener {
            binding.drawerLayout.open()
        }


        binding.shareLinearGita.setOnClickListener {
            val gitaSanskritText = binding.tvGitaSonskrit.text.toString()
            val gitaEnglishText = binding.tvGitaEnglish.text.toString()
            val shareText = "$gitaSanskritText\n$gitaEnglishText"

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share With"))

        }
        binding.shareLinearVedas.setOnClickListener {
            val vedaSanskritText = binding.tvVedaSanskrit.text.toString()
            val vedaEnglishText = binding.tvVedasEnglish.text.toString()
            val shareText = "$vedaSanskritText\n$vedaEnglishText"

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share With"))

        }

        binding.shareLinearSloka.setOnClickListener {
            val slokaSanskritText = binding.tvSlokaSonskrit.text.toString()
            val slokaEnglishText = binding.tvSlokaEnglish.text.toString()
            val shareText = "$slokaSanskritText\n$slokaEnglishText"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share With"))

        }
        binding.readVedas.setOnClickListener {

            val textSons = binding.tvVedaSanskrit.text.toString()
            val textEnglish = binding.tvVedasEnglish.text.toString()
            val intent = Intent(requireContext(),ReadActivity::class.java)
            intent.putExtra("TEXT_SONSKRIT",textSons)
            intent.putExtra("TEXT_ENGLISH",textEnglish)
            requireContext().startActivity(intent)
        }
        binding.readLinearGita.setOnClickListener {

            val textSons = binding.tvGitaSonskrit.text.toString()
            val textEnglish = binding.tvGitaEnglish.text.toString()
            val intent = Intent(requireContext(),ReadActivity::class.java)
            intent.putExtra("TEXT_SONSKRIT",textSons)
            intent.putExtra("TEXT_ENGLISH",textEnglish)
            requireContext().startActivity(intent)
        }
        binding.readLinearSloka.setOnClickListener {

            val textSons = binding.tvSlokaSonskrit.text.toString()
            val textEnglish = binding.tvSlokaEnglish.text.toString()
            val intent = Intent(requireContext(),ReadActivity::class.java)
            intent.putExtra("TEXT_SONSKRIT",textSons)
            intent.putExtra("TEXT_ENGLISH",textEnglish)
            requireContext().startActivity(intent)
        }



       /* binding.scrollMain.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.scrollMain.scrollY

            if (scrollY > lastScrollY) {
                // Scrolling down
                binding.constraintLayout5.visibility = View.VISIBLE
            } else if (scrollY < lastScrollY) {
                // Scrolling up
                binding.constraintLayout5.visibility = View.GONE
            }

            lastScrollY = scrollY
        }*/


        if (isNetworkAvailable(requireContext())) {
            // Internet is available, retrieve data
            getAdmin()
            getVedas()
            getGita()
            getSloka()
        } else {
            // No internet connection, show dialog
            showNoInternetDialog()
        }



        binding.linearTranslateVedas.setOnClickListener {
            showLanguageVedaSelectorDialog()
        }

        binding.linearTranslateGita.setOnClickListener {
            showLanguageGitaSelectorDialog()
        }

        binding.linearTranslateSloka.setOnClickListener {
            showLanguageSlokaSelectorDialog()
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


    private fun showLanguageGitaSelectorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_language_selector, null)
        val spinnerLanguages = dialogView.findViewById<Spinner>(R.id.spinnerLanguages)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                val selectedLanguage = spinnerLanguages.selectedItem.toString()
                translateTextGita(selectedLanguage)
            }
            .setNegativeButton("Cancel", null)
            .show()
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
    private fun showLanguageSlokaSelectorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_language_selector, null)
        val spinnerLanguages = dialogView.findViewById<Spinner>(R.id.spinnerLanguages)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                val selectedLanguage = spinnerLanguages.selectedItem.toString()
                translateTextSloka(selectedLanguage)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



    private fun getAdmin() {
        val uid = auth.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().reference.child("Admin")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isAdmin = false
                for (childSnapshot in snapshot.children) {
                    if (childSnapshot.key == uid) {
                        isAdmin = true
                        break
                    }
                }
                if (isAdmin) {
                    binding.addSloka.visibility = View.VISIBLE
                }
                else {
                    binding.addSloka.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    private fun getSloka() {
        val db = FirebaseDatabase.getInstance().reference.child("Sloka").child("Others Sloka")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childCount = dataSnapshot.childrenCount.toInt()

                if (childCount > 0) {
                    val randomInt = Random.nextInt(childCount)

                    var currentIndex = 0
                    for (child in dataSnapshot.children) {
                        if (currentIndex == randomInt) {
                            val sloka = child.getValue(Sloka::class.java)
                            sloka?.let {
                                displayOtherSloka(it)
                            }
                            break
                        }
                        currentIndex++
                    }
                } else {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to get child count", Toast.LENGTH_SHORT).show()
            }
        })



    }

    private fun getGita() {

        val db = FirebaseDatabase.getInstance().reference.child("Sloka").child("Gitas")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childCount = dataSnapshot.childrenCount.toInt()

                if (childCount > 0) {
                    val randomInt = Random.nextInt(childCount)

                    var currentIndex = 0
                    for (child in dataSnapshot.children) {
                        if (currentIndex == randomInt) {
                            val sloka = child.getValue(Sloka::class.java)
                            sloka?.let {
                                displaySlokaGita(it)
                            }
                            break
                        }
                        currentIndex++
                    }
                } else {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to get child count", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getVedas() {

        val db = FirebaseDatabase.getInstance().reference.child("Sloka").child("Vedas")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childCount = dataSnapshot.childrenCount.toInt()

                if (childCount > 0) {
                    val randomInt = Random.nextInt(childCount)

                    var currentIndex = 0
                    for (child in dataSnapshot.children) {
                        if (currentIndex == randomInt) {
                            val sloka = child.getValue(Sloka::class.java)
                            sloka?.let {
                                displaySlokaVedas(it)
                            }
                            break
                        }
                        currentIndex++
                    }
                } else {

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to get child count", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun displaySlokaVedas(sloka: Sloka) {
        binding.tvVedaSource.text = sloka.slokaSourse
        binding.tvVedaSanskrit.text = sloka.slokaSanskrit
        binding.tvVedasEnglish.text = sloka.slokaEnglish
    }
    private fun displaySlokaGita(sloka: Sloka) {
        binding.tvGitaSource.text = sloka.slokaSourse
        binding.tvGitaSonskrit.text = sloka.slokaSanskrit
        binding.tvGitaEnglish.text = sloka.slokaEnglish
    }
    private fun displayOtherSloka(sloka: Sloka) {
        binding.tvSlukaSource.text = sloka.slokaSourse
        binding.tvSlokaSonskrit.text = sloka.slokaSanskrit
        binding.tvSlokaEnglish.text = sloka.slokaEnglish
    }

    private fun showLanguageVedaSelectorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_language_selector, null)
        val spinnerLanguages = dialogView.findViewById<Spinner>(R.id.spinnerLanguages)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguages.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                val selectedLanguage = spinnerLanguages.selectedItem.toString()
                translateTextVedas(selectedLanguage)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun translateTextGita(language: String) {
        val sourceLanguage = "en" // Assume the source text is in English
        val targetLanguage = getLanguageCode(language)

        if (targetLanguage != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            val translator: com.google.mlkit.nl.translate.Translator =  Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(binding.tvGitaEnglish.text.toString())
                        .addOnSuccessListener { translatedText ->
                            binding.tvGitaEnglish.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                            binding.tvGitaEnglish.text = "Translation failed: ${exception.message}"
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle error
                    binding.tvGitaEnglish.text = "Model download failed: ${exception.message}"
                }
        } else {
            binding.tvGitaEnglish.text = "Unsupported language"
        }
    }


    private fun translateTextVedas(language: String) {
        val sourceLanguage = "en" // Assume the source text is in English
        val targetLanguage = getLanguageCode(language)

        if (targetLanguage != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            val translator: com.google.mlkit.nl.translate.Translator =  Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(binding.tvVedasEnglish.text.toString())
                        .addOnSuccessListener { translatedText ->
                            binding.tvVedasEnglish.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                            binding.tvVedasEnglish.text = "Translation failed: ${exception.message}"
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle error
                    binding.tvVedasEnglish.text = "Model download failed: ${exception.message}"
                }
        } else {
            binding.tvVedasEnglish.text = "Unsupported language"
        }
    }




    private fun getLanguageCode(language: String): String? {
        return when (language) {
            "Spanish" -> "es"
            "French" -> "fr"
            "Hindi" -> "hi"
            "Bengali" -> "bn"
            "Telugu" -> "te"
            "Marathi" -> "mr"
            "Tamil" -> "ta"
            "Gujarati" -> "gu"
            "Urdu" -> "ur"
            "Kannada" -> "kn"
            "Malayalam" -> "ml"
            "Odia" -> "or"
            "Punjabi" -> "pa"
            "Assamese" -> "as"
            "Maithili" -> "mai"
            "Santali" -> "sat"
            "Konkani" -> "kok"
            "Russian" -> "ru"
            "Arabic" -> "ar"
            "Sinhalese" -> "si"
            "Nepali" -> "ne"
            "Dzongkha" -> "dz"
            "Burmese" -> "my"
            // Add other languages and their codes as needed
            else -> null
        }
    }
    private fun translateTextSloka(language: String) {
        val sourceLanguage = "en" // Assume the source text is in English
        val targetLanguage = getLanguageCode(language)

        if (targetLanguage != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            val translator: com.google.mlkit.nl.translate.Translator =  Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(binding.tvSlokaEnglish.text.toString())
                        .addOnSuccessListener { translatedText ->
                            binding.tvSlokaEnglish.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                            binding.tvSlokaEnglish.text = "Translation failed: ${exception.message}"
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle error
                    binding.tvSlokaEnglish.text = "Model download failed: ${exception.message}"
                }
        } else {
            binding.tvSlokaEnglish.text = "Unsupported language"
        }
    }





}