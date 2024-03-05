package com.appsdeviser.workmanager

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.UUID

class PhotoViewModel: ViewModel() {

    var unCompressedImageUri: Uri? by mutableStateOf(null)
        private set

    var compressedBitmap: Bitmap? by mutableStateOf(null)
        private set

    var workId: UUID? by mutableStateOf(null)
        private set

    fun updateUnCompressedUri(uri: Uri?){
        unCompressedImageUri = uri
    }

    fun updateCompressedBitmap(bmp: Bitmap?) {
        compressedBitmap = bmp
    }

    fun updateWorkId(uuid: UUID?) {
        workId = uuid
    }

    fun clear() {
        unCompressedImageUri = null
        compressedBitmap = null
        workId = null
    }
}