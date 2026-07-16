package com.uriel.logpose.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uriel.logpose.AppContainer

class BluetoothViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {

            return BluetoothViewModel() as T

        }

        throw IllegalArgumentException(
            "Unknown ViewModel: ${modelClass.name}"
        )

    }

}