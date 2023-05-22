package com.aws.picsync.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aws.picsync.model.ImageModel
import com.aws.picsync.ui.components.TopAppBar
import com.aws.picsync.ui.theme.PicsyncTheme

class MainActivity : ComponentActivity() {
    private val image = ImageModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PicsyncTheme {
                MyAppContent()
            }
        }
    }

    @Composable
    fun MyAppContent() {
        Column {
            TopAppBar()
            GalleryButton()
        }
    }

    @Composable
    fun GalleryButton() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    resultLauncher.launch(galleryIntent)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
                    resultLauncher.launch(galleryIntent)
                }
            }
        ) {
            Text(text = "Open Gallery")
        }
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val clipData = data.clipData
                if (clipData != null) {
                    image.handleMultipleImageSelection(clipData, contentResolver)
                } else {
                    val imageUri: Uri? = data.data
                    if (imageUri != null) {
                        image.handleSingleImageSelection(imageUri, contentResolver)
                    }
                }
            }
        }
    }
}