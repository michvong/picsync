package com.aws.picsync.ui.components

import android.content.ContentResolver
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.aws.picsync.model.ImageModel

private val image = ImageModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(selectedPhotos: SnapshotStateList<Int>, contentResolver: ContentResolver) {
    TopAppBar(
        title = {
            Text(
                text = "PicSync",
                color = MaterialTheme.colorScheme.primary)
        },

        navigationIcon = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },

        actions = {
            IconButton(onClick = { image.handleImageSend(selectedPhotos, contentResolver)}) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}
