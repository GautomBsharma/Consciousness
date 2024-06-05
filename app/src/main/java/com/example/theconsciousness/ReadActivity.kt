package com.example.theconsciousness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.theconsciousness.databinding.ActivityReadBinding
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class ReadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadBinding
    private var textSon = ""
    private var teextEng = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textSon = intent.getStringExtra("TEXT_SONSKRIT").toString()
        teextEng = intent.getStringExtra("TEXT_ENGLISH").toString()
        binding.tvShowSanskrit.text = textSon

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguageRead.adapter = adapter

        binding.spinnerLanguageRead.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                if (selectedLanguage.isNotEmpty()) {
                    translateTextGita(selectedLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun translateTextGita(language: String) {
        val sourceLanguage = "en" // Assume the source text is in English
        val targetLanguage = getLanguageCode(language)

        if (targetLanguage != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(teextEng)
                        .addOnSuccessListener { translatedText ->
                            binding.tvShowEnglish.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            binding.tvShowEnglish.text = "Translation failed: ${exception.message}"
                        }
                }
                .addOnFailureListener { exception ->
                    binding.tvShowEnglish.text = "Model download failed: ${exception.message}"
                }
        } else {
            binding.tvShowEnglish.text = teextEng
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
}