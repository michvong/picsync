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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
fun GalleryScreen(contentResolver: ContentResolver, innerPadding: PaddingValues) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    LaunchedEffect(permissionState) {
        permissionState.launchPermissionRequest()
    }
    
    PhotoGrid(contentResolver = contentResolver, innerPadding = innerPadding)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoGrid(contentResolver: ContentResolver, innerPadding: PaddingValues) {
    val galleryPaths = image.getGalleryPhotos(contentResolver)

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
                        .clickable { println("hi") }
                )
            }
        }
    }
}

