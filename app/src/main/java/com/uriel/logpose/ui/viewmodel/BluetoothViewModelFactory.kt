package com.uriel.logpose.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uriel.logpose.domain.repositories.BluetoothRepository



class BluetoothViewModelFactory(
    private val repository: BluetoothRepository
) : ViewModelProvider.Factory {



    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {


        if(
            modelClass.isAssignableFrom(
                BluetoothViewModel::class.java
            )
        ){

            return BluetoothViewModel(
                repository
            ) as T

        }



        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )


    }


}