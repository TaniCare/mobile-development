package com.dicoding.tanicare.ui.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.tanicare.data.model.PredictionResponse
import com.dicoding.tanicare.databinding.ActivityResultBinding
import com.dicoding.tanicare.ui.main.MenuActivity
import com.dicoding.tanicare.viewmodel.ViewModelFactory
import java.io.File

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("imagePath")
        if (filePath != null) {
            displayPredictedImage(filePath)
            viewModel.processImage(filePath)
        } else {
            Toast.makeText(this, "No image to analyze", Toast.LENGTH_SHORT).show()
            finish()
        }

        observeViewModel()
    }

    private fun displayPredictedImage(filePath: String) {
        val fileUri = Uri.fromFile(File(filePath))
        binding.ivPredictedImage.setImageURI(fileUri)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.lottieLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.predictionResult.observe(this) { result ->
            result.onSuccess { prediction ->
                displayResult(prediction)
                viewModel.saveHistory(prediction, intent.getStringExtra("imagePath") ?: "")
            }.onFailure { exception ->
                showToast("Error occured. Please try again.")
                navigateToMainMenu()
            }
        }
    }


    private fun navigateToMainMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun displayResult(prediction: PredictionResponse) {
        binding.tvAccuracy.text = "Accuracy: ${prediction.accuracy}"

        val diseaseName = prediction.disease_name
        val scientificName = diseaseName?.scientific ?: "Unknown"
        val englishName = diseaseName?.english ?: "Unknown"
        val indonesianName = diseaseName?.indonesian ?: "Unknown"

        binding.tvLabel.text = Html.fromHtml(
            """
        <font color='#DC143C'><b>Penyakit:</b></font><br>
        - Scientific: $scientificName<br>
        - English: $englishName<br>
        - Indonesian: $indonesianName
        """.trimIndent(), Html.FROM_HTML_MODE_LEGACY
        )

        binding.tvApplication.text = Html.fromHtml(
            """
        <font color='#006400'><b>Penanganan:</b></font> ${prediction.application ?: "No application provided"}
        """.trimIndent(), Html.FROM_HTML_MODE_LEGACY
        )

        prediction.symptoms?.let { symptoms ->
            val formattedSymptoms = symptoms.split(",").joinToString(separator = "<br>• ") { it.trim() }
            binding.tvSymptoms.text = Html.fromHtml(
                """
            <font color='#DC143C'><b>Tanda dan Gejala:</b></font><br>• $formattedSymptoms
            """.trimIndent(), Html.FROM_HTML_MODE_LEGACY
            )
        }

        prediction.medicines?.let { medicines ->
            val medicineText = StringBuilder("<font color='#006400'><b>Obat-obatan:</b></font><br>")
            medicines.forEach { medicine ->
                medicineText.append("<font color='#006400'• <b>${medicine.name}</b></font><br>")
                medicine.purchase_links.forEach { link ->
                    medicineText.append("- <a href=\"$link\">$link</a><br>")
                }
            }
            binding.tvMedicines.text = Html.fromHtml(medicineText.toString(), Html.FROM_HTML_MODE_LEGACY)
            binding.tvMedicines.movementMethod = LinkMovementMethod.getInstance()
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
