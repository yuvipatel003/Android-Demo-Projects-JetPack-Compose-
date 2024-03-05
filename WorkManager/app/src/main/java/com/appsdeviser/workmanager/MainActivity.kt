package com.appsdeviser.workmanager

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.appsdeviser.workmanager.PhotoCompressionWorker.Companion.KEY_COMPRESSION_THRESHOLD
import com.appsdeviser.workmanager.PhotoCompressionWorker.Companion.KEY_CONTENT_URI
import com.appsdeviser.workmanager.PhotoCompressionWorker.Companion.KEY_RESULT_PATH
import com.appsdeviser.workmanager.ui.theme.WorkManagerTheme

class MainActivity : ComponentActivity() {

    private lateinit var workManager: WorkManager
    private val photoViewModel by viewModels<PhotoViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(applicationContext)
        setContent {
            WorkManagerTheme {
                val workResult = photoViewModel.workId?.let { id ->
                    workManager.getWorkInfoByIdLiveData(id).observeAsState().value
                }

                LaunchedEffect(key1 = workResult?.outputData) {
                    if(workResult?.outputData != null) {
                        val filePath = workResult.outputData.getString(
                            KEY_RESULT_PATH
                        )
                        filePath?.let {
                            val bitmap = BitmapFactory.decodeFile(filePath)
                            photoViewModel.updateCompressedBitmap(bitmap)
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    photoViewModel.unCompressedImageUri?.let {
                        Text(text = "Actual Original Image")
                        AsyncImage(model = it, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    photoViewModel.compressedBitmap?.let {
                        Text(text = "Compressed Image")
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        photoViewModel.clear()
        val uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        } ?: return
        photoViewModel.updateUnCompressedUri(uri)

        val request = OneTimeWorkRequestBuilder<PhotoCompressionWorker>()
            .setInputData(
                workDataOf(
                    KEY_CONTENT_URI to uri.toString(),
                    KEY_COMPRESSION_THRESHOLD to 1024 * 20L
                )
            )
            .build()
        photoViewModel.updateWorkId(request.id)
        workManager.enqueue(request)
    }
}
