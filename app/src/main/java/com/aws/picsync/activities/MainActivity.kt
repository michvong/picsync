package com.aws.picsync.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aws.picsync.R
import com.aws.picsync.utils.S3Methods
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val s3Methods = S3Methods();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val galleryButton = findViewById<Button>(R.id.galleryButton);
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                resultLauncher.launch(galleryIntent);
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1);
                resultLauncher.launch(galleryIntent);
            }
        }
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            val imagePath =  getPathFromURI(imageUri)
            println("Image path: $imagePath")

            if (imagePath != null) {
                val fileName = File(imagePath).name

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        s3Methods.createNewBucket(bucketName = dotenv["BUCKET_NAME"]);
                        s3Methods.uploadImage(
                            bucketName = dotenv["BUCKET_NAME"],
                            objectKey = fileName,
                            objectPath = imagePath
                        )
                    }
                }
            }
        }
    }

    private fun getPathFromURI(uri: Uri?): String? {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

}