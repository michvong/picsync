package com.aws.picsync.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    TopAppBar(
        title = { Text(text = "PicSync") }
    )
}
