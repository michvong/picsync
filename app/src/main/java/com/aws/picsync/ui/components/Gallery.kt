package com.aws.picsync.ui.components

import android.Manifest
import android.content.ContentResolver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aws.picsync.model.ImageModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

private val image = ImageModel()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryScreen(contentResolver: ContentResolver, innerPadding: PaddingValues, selectedPhotos: SnapshotStateList<Int>) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)

    LaunchedEffect(permissionState) {
        permissionState.launchPermissionRequest()
    }


    when {
        permissionState.hasPermission -> {
            PhotoGrid(contentResolver = contentResolver, innerPadding = innerPadding, selectedPhotos = selectedPhotos)
        }
        permissionState.shouldShowRationale -> {
            println("To order to access PicSync utilities, permission to access media is required.")
        }
        else -> {
            // Permission denied, handle accordingly
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhotoGrid(contentResolver: ContentResolver, innerPadding: PaddingValues, selectedPhotos: SnapshotStateList<Int>) {
    val galleryPaths = remember { image.getGalleryPhotos(contentResolver) }

    LazyVerticalGrid(
        modifier = Modifier.consumeWindowInsets(innerPadding),
        columns = GridCells.Fixed(4),
        contentPadding = innerPadding
    ) {
        items(galleryPaths.size) { index ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
            ) {
                val isSelected = selectedPhotos.contains(index)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(galleryPaths[index])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(0.5.dp)
                        .clickable {
                            toggleSelection(selectedPhotos = selectedPhotos, index = index)
                        }
                )
                if (isSelected) {
                    SelectedPhoto()
                }
            }
        }
    }
}

private fun toggleSelection(selectedPhotos: SnapshotStateList<Int>, index: Int) {
    if (selectedPhotos.contains(index)) {
        selectedPhotos.remove(index)
    } else {
        selectedPhotos.add(index)
    }
}

@Composable
private fun SelectedPhoto() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color.White
            )
        }
}