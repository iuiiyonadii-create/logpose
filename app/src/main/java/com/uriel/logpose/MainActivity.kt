package com.uriel.logpose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.uriel.logpose.ui.LogPoseScreen
import com.uriel.logpose.ui.theme.LogPoseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LogPoseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LogPoseScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}