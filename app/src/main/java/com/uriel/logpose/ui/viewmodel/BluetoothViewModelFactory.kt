package com.uriel.logpose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BluetoothViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                BluetoothViewModel::class.java
            )) {

            return BluetoothViewModel() as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}