package com.uriel.logpose.core.di

import android.content.Context
import com.uriel.logpose.core.bluetooth.BluetoothManager
import com.uriel.logpose.data.bluetooth.BluetoothRepositoryImpl
import com.uriel.logpose.domain.repositories.BluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return BluetoothManager(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothRepository(
        @ApplicationContext context: Context
    ): BluetoothRepository {
        return BluetoothRepositoryImpl(context) as BluetoothRepository
    }
}
