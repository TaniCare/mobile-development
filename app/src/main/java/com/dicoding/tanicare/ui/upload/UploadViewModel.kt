package com.dicoding.tanicare.ui.upload

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel

class UploadViewModel : ViewModel() {
    var imageUri: Uri? = null
    var capturedImage: Bitmap? = null
}
