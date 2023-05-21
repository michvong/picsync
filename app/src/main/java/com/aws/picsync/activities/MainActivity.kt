package com.aws.picsync.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.aws.picsync.R
import com.aws.picsync.utils.S3Methods
import java.io.File


class MainActivity : ComponentActivity() {
    private val s3Methods = S3Methods();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val galleryButton = findViewById<Button>(R.id.galleryButton);
        galleryButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            resultLauncher.launch(galleryIntent);
        }
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            val imagePath =  getPathFromURI(imageUri)
            println("Image path: $imagePath")
        }
    }

    private fun getPathFromURI(uri: Uri?): String? {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

}