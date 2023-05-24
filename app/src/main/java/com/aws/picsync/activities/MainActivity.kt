package com.aws.picsync.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.aws.picsync.ui.components.GalleryScreen
import com.aws.picsync.ui.components.TopAppBar
import com.aws.picsync.ui.theme.PicsyncTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PicsyncTheme {
                ActivityScreen()
            }
        }
    }

    @ExperimentalFoundationApi
    @Composable
    fun ActivityScreen() {
        Scaffold(
            topBar = {
                TopAppBar()
            },
            content = { innerPadding ->
                GalleryScreen(contentResolver = contentResolver, innerPadding = innerPadding)
            }
        )
    }
}