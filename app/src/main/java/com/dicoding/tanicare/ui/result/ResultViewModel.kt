package com.dicoding.tanicare.ui.result

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tanicare.data.local.HistoryEntity
import com.dicoding.tanicare.data.model.PredictionResponse
import com.dicoding.tanicare.data.repository.ResultRepository
import kotlinx.coroutines.launch

class ResultViewModel(context: Context) : ViewModel() {

    private val repository: ResultRepository = ResultRepository.getInstance(context)

    private val _predictionResult = MutableLiveData<Result<PredictionResponse>>()
    val predictionResult: LiveData<Result<PredictionResponse>> = _predictionResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun processImage(filePath: String) {
        _isLoading.postValue(true)
        repository.processImage(filePath) { result ->
            _isLoading.postValue(false)
            _predictionResult.postValue(result)
        }
    }

    fun saveHistory(prediction: PredictionResponse, imagePath: String) {
        val history = HistoryEntity(
            diseaseName = prediction.disease_name?.english ?: "Unknown",
            accuracy = prediction.accuracy,
            timestamp = System.currentTimeMillis(),
            imagePath = imagePath
        )
        viewModelScope.launch {
            repository.saveHistory(history)
        }
    }
}
