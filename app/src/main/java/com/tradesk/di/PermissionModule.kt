package com.tradesk.di

import android.content.Context
import com.tradesk.util.PermissionFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PermissionModule {
    @Provides
    @Singleton
    internal fun provideCache(@ApplicationContext context: Context): PermissionFile {
        return PermissionFile(context)
    }
}