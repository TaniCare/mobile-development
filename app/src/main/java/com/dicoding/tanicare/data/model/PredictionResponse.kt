package com.dicoding.tanicare.data.model

data class PredictionResponse(
    val accuracy: String,
    val label: String? = null,
    val error: String? = null,
    val imageDetails: ImageDetails? = null,
    val application: String? = null,
    val disease_name: DiseaseName? = null,
    val immediate: List<String>? = null,
    val medicines: List<Medicine>? = null,
    val symptoms: String? = null
)

data class DiseaseName(
    val english: String,
    val indonesian: String,
    val scientific: String
)

data class Medicine(
    val name: String,
    val purchase_links: List<String>
)

data class ImageDetails(
    val format: String,
    val height: Int,
    val width: Int
)

