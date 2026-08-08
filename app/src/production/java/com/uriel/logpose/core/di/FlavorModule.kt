package com.uriel.logpose.core.di

import com.uriel.logpose.core.network.PCBridge
import com.uriel.logpose.core.network.PCBridgeProdImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {

    @Binds
    @Singleton
    abstract fun bindPCBridge(impl: PCBridgeProdImpl): PCBridge
}
