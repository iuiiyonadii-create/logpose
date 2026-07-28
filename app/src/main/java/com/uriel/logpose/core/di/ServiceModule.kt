package com.uriel.logpose.core.di

import android.content.Context
import com.uriel.logpose.services.ServiceNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideServiceNotificationManager(@ApplicationContext context: Context): ServiceNotificationManager {
        return ServiceNotificationManager(context)
    }
}
