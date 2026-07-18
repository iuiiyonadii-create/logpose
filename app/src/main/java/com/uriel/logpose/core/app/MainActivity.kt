package com.uriel.logpose.core.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.uriel.logpose.ui.screen.LogPoseScreen
import com.uriel.logpose.ui.theme.LogPoseTheme


class MainActivity : ComponentActivity() {


    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }





    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        AppContainer.initialize(
            applicationContext
        )


        requestPermissions()


        enableEdgeToEdge()



        setContent {


            LogPoseTheme {


                Scaffold(

                    modifier = Modifier.fillMaxSize()

                ) { innerPadding ->


                    LogPoseScreen(

                        modifier =
                            Modifier.padding(
                                innerPadding
                            )

                    )


                }


            }


        }


    }





    private fun requestPermissions() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {


            permissionLauncher.launch(

                arrayOf(

                    Manifest.permission.BLUETOOTH_CONNECT,

                    Manifest.permission.BLUETOOTH_SCAN,

                    Manifest.permission.RECORD_AUDIO

                )

            )


        } else {


            permissionLauncher.launch(

                arrayOf(

                    Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.RECORD_AUDIO

                )

            )


        }


    }


}